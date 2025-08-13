package com.assu.server.domain.partnership.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
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
import com.assu.server.global.exception.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
    public List<PartnershipResponseDTO.WritePartnershipResponseDTO> listPartnerships(boolean all) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        List<Paper> papers;

        if (all) {
            papers = paperRepository.findAll(sort);
        } else {
            papers = paperRepository.findAll(PageRequest.of(0, 2, sort)).getContent();
        }
        if (papers.isEmpty()) return List.of();

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
}
