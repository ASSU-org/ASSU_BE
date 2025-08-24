package com.assu.server.domain.partnership.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.domain.partnership.converter.PartnershipConverter;
import com.assu.server.domain.partnership.dto.PartnershipRequestDTO;
import com.assu.server.domain.partnership.dto.PartnershipResponseDTO;
import com.assu.server.domain.partnership.entity.Goods;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.repository.GoodsRepository;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.partnership.repository.PaperRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.config.AmazonConfig;
import com.assu.server.global.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartnershipServiceImpl implements PartnershipService {

    private final PaperRepository paperRepository;
    private final PaperContentRepository paperContentRepository;
    private final GoodsRepository goodsRepository;

    private final AdminRepository adminRepository;
    private final PartnerRepository partnerRepository;
    private final StoreRepository storeRepository;

    private final S3Presigner presigner;
    private final AmazonConfig amazonConfig;

    @Override
    public PartnershipResponseDTO.WritePartnershipResponseDTO writePartnership(PartnershipRequestDTO.WritePartnershipRequestDTO request) {

        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        Partner partner = partnerRepository.findById(request.getPartnerId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));
        Store store = storeRepository.findById(request.getStoreId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_MEMBER));

        Paper paper = PartnershipConverter.toPaperEntity(request, admin, partner, store);
        paper = paperRepository.save(paper);

        List<PaperContent> contents = PartnershipConverter.toPaperContents(request, paper);
        contents = contents.isEmpty() ? contents : paperContentRepository.saveAll(contents);

        List<List<Goods>> requestGoodsBatches = PartnershipConverter.toGoodsBatches(request);

        List<List<Goods>> attachedGoodsBatches = new ArrayList<>();
        List<Goods> toPersist = new ArrayList<>();

        for(int i = 0;i < contents.size();i++){
            PaperContent content = contents.get(i);
            List<Goods> batch = (requestGoodsBatches.size() > i) ? requestGoodsBatches.get(i) : Collections.emptyList();

            List<Goods> attached = new ArrayList<>(batch.size());
            for(Goods g : batch){
                Goods entity = Goods.builder()
                        .content(content)
                        .belonging(g.getBelonging())
                        .build();
                attached.add(entity);
                toPersist.add(entity);
            }
            attachedGoodsBatches.add(attached);
        }

        if(!toPersist.isEmpty()){
            goodsRepository.saveAll(toPersist);
        }

        return PartnershipConverter.writePartnershipResultDTO(paper, contents, attachedGoodsBatches);
    }

    @Override
    public List<PartnershipResponseDTO.WritePartnershipResponseDTO> listPartnershipsForAdmin(boolean all) {
//        Long adminId = SecurityUtil.getCurrentUserId();
        Long adminId = 1L;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Paper> papers = all
                ? paperRepository.findByAdmin_IdAndIsActivated(adminId, ActivationStatus.ACTIVE, sort)
                : paperRepository.findByAdmin_IdAndIsActivated(adminId, ActivationStatus.ACTIVE, PageRequest.of(0, 2, sort)).getContent();

        papers = papers.stream()
                .filter(p -> p.getStore() != null)
                .toList();

        return buildPartnershipDTOs(papers);
    }

    @Override
    public List<PartnershipResponseDTO.WritePartnershipResponseDTO> listPartnershipsForPartner(boolean all) {
        //        Long partnerId = SecurityUtil.getCurrentUserId();
        Long partnerId = 3L;

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Paper> papers = all
                ? paperRepository.findByPartner_IdAndIsActivated(partnerId, ActivationStatus.ACTIVE, sort)
                : paperRepository.findByPartner_IdAndIsActivated(partnerId, ActivationStatus.ACTIVE, PageRequest.of(0, 2, sort)).getContent();

        papers = papers.stream()
                .filter(p -> p.getAdmin() != null)
                .toList();

        return buildPartnershipDTOs(papers);
    }

    @Override
    public PartnershipResponseDTO.WritePartnershipResponseDTO getPartnership(Long partnershipId) {
        Paper paper = paperRepository.findById(partnershipId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PAPER));

        List<PaperContent> contents = paperContentRepository.findAllByOnePaperIdInFetchGoods(partnershipId);

        List<List<Goods>> goodsBatches = contents.stream()
                .map(pc -> pc.getGoods() == null ? Collections.<Goods>emptyList() : pc.getGoods())
                .toList();

        return PartnershipConverter.writePartnershipResultDTO(paper, contents, goodsBatches);
    }

    @Override
    @Transactional
    public PartnershipResponseDTO.UpdateResponseDTO updatePartnershipStatus(Long partnershipId, PartnershipRequestDTO.UpdateRequestDTO request) {
        Paper paper = paperRepository.findById(partnershipId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PAPER));

        if(request == null || request.getStatus() == null){
            throw new DatabaseException(ErrorStatus.INVALID_REQUEST);
        }

        ActivationStatus prev = paper.getIsActivated();
        ActivationStatus next = parseStatus(request.getStatus());

        paper.setIsActivated(next);

        return PartnershipResponseDTO.UpdateResponseDTO.builder()
                .partnershipId(paper.getId())
                .prevStatus(prev == null ? null : prev.name())
                .newStatus(next.name())
                .changedAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public PartnershipResponseDTO.ManualPartnershipResponseDTO createManualPartnership(PartnershipRequestDTO.ManualPartnershipRequestDTO request, String filename, String contentType) {
        if(request == null || request.getAdminId() == null || request.getStoreAddress() == null) throw new DatabaseException(ErrorStatus.INVALID_REQUEST);

        Admin admin = adminRepository.findById(request.getAdminId())
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

        Store store = storeRepository
                .findByNameAndAddressAndDetailAddress(request.getStoreName(), request.getStoreAddress(), request.getStoreDetailAddress())
                .orElse(null);

        boolean created = false;
        boolean reactivated = false;

        if (store == null) {
            store = Store.builder()
                    .name(request.getStoreName())
                    .address(request.getStoreAddress())
                    .detailAddress(request.getStoreDetailAddress())
                    .rate(0)
                    .isActivate(ActivationStatus.SUSPEND)
                    .build();
            store = storeRepository.save(store);
            created = true;
        } else {
            if(store.getIsActivate() == ActivationStatus.INACTIVE) {
                store.setIsActivate(ActivationStatus.SUSPEND);
                reactivated = true;
            }
        }

        Presigned presigned = null;
        if (filename != null && !filename.isBlank()) {
            presigned = putUrlForStore(
                    store.getId(), filename,
                    (contentType == null || contentType.isBlank()) ? "image/jpeg" : contentType,
                    Duration.ofMinutes(10)
            );
        }

        Paper paper = Paper.builder()
                .admin(admin)
                .store(store)
                .partner(null)
                .isActivated(ActivationStatus.SUSPEND)
                .partnershipPeriodStart(request.getPartnershipPeriodStart())
                .partnershipPeriodEnd(request.getPartnershipPeriodEnd())
                .build();
        paper = paperRepository.save(paper);

        List<PaperContent> contents = new ArrayList<>();
        if (request.getOptions() != null) {
            for (PartnershipRequestDTO.PartnershipOptionRequestDTO o : request.getOptions()) {
                PaperContent content = PaperContent.builder()
                        .paper(paper)
                        .optionType(o.getOptionType())
                        .criterionType(o.getCriterionType())
                        .people(o.getPeople())
                        .cost(o.getCost())
                        .category(o.getCategory())
                        .discount(o.getDiscountRate())
                        .build();
                content = paperContentRepository.save(content);

                if(o.getGoods() != null && !o.getGoods().isEmpty()) {
                    List<Goods> batch = new ArrayList<>(o.getGoods().size());
                    for (var g : o.getGoods()) {
                        Goods entity = Goods.builder()
                                .content(content)
                                .belonging(g.getGoodsName())
                                .build();
                        batch.add(entity);
                    }
                    goodsRepository.saveAll(batch);
                }
                contents.add(content);
            }
        }

        List<List<Goods>> goodsBatches = contents.stream()
                .map(pc -> pc.getGoods() == null ? Collections.<Goods>emptyList() : pc.getGoods())
                .toList();

        PartnershipResponseDTO.WritePartnershipResponseDTO partnershipResponseDTO =
                PartnershipConverter.writePartnershipResultDTO(paper, contents, goodsBatches);

        return PartnershipResponseDTO.ManualPartnershipResponseDTO.builder()
                .storeId(store.getId())
                .storeCreated(created)
                .storeActivated(reactivated)
                .status(store.getIsActivate() == null ? null : store.getIsActivate().name())
                .contractImageUrl(presigned == null ? null : presigned.getUrl())
                .objectKey(presigned == null ? null : presigned.getKey())
                .partnership(partnershipResponseDTO)
                .build();
    }

    private List<PartnershipResponseDTO.WritePartnershipResponseDTO> buildPartnershipDTOs(List<Paper> papers) {
        if (papers == null || papers.isEmpty()) return List.of();

        List<Long> paperIds = papers.stream().map(Paper::getId).toList();
        List<PaperContent> allContents = paperContentRepository.findAllByPaperIdInFetchGoods(paperIds);

        Map<Long, List<PaperContent>> byPaperId = allContents.stream()
                .collect(Collectors.groupingBy(pc -> pc.getPaper().getId()));

        List<PartnershipResponseDTO.WritePartnershipResponseDTO> result = new ArrayList<>(papers.size());
        for (Paper p : papers) {
            List<PaperContent> contents = byPaperId.getOrDefault(p.getId(), List.of());
            List<List<Goods>> goodsBatches = contents.stream()
                    .map(pc -> pc.getGoods() == null ? List.<Goods>of() : pc.getGoods())
                    .toList();
            result.add(PartnershipConverter.writePartnershipResultDTO(p, contents, goodsBatches));
        }
        return result;
    }

    private ActivationStatus parseStatus(String raw) {
        try {
            return ActivationStatus.valueOf(raw.trim().toUpperCase());
        } catch (Exception e) {
            throw new DatabaseException(ErrorStatus.INVALID_REQUEST);
        }
    }

    public Presigned putUrlForStore(Long storeId, String filename, String contentType, Duration ttl) {
        String key = "stores/" + storeId + "/" + UUID.randomUUID() + "_" + filename;
        return presignPut(key, contentType, ttl);
    }

    public Presigned putUrlForPartnership(Long paperId, String filename, String contentType, Duration ttl) {
        String key = "partnerships/" + paperId + "/" + UUID.randomUUID() + "_" + filename;
        return presignPut(key, contentType, ttl);
    }

    private Presigned presignPut(String key, String contentType, Duration ttl) {
        PutObjectRequest por = PutObjectRequest.builder()
                .bucket(amazonConfig.getBucket())
                .key(key)
                .contentType(contentType)
                .build();

        PutObjectPresignRequest preq = PutObjectPresignRequest.builder()
                .signatureDuration(ttl == null ? Duration.ofMinutes(10) : ttl)
                .putObjectRequest(por)
                .build();

        PresignedPutObjectRequest p = presigner.presignPutObject(preq);
        return new Presigned(key, p.url().toString());
    }

    @Getter @AllArgsConstructor
    public static class Presigned {
        private String key;
        private String url;
    }
}
