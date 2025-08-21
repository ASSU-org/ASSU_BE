package com.assu.server.domain.map.service;

import com.assu.server.domain.admin.entity.Admin;
import com.assu.server.domain.admin.repository.AdminRepository;
import com.assu.server.domain.common.enums.ActivationStatus;
import com.assu.server.domain.map.converter.MapConverter;
import com.assu.server.domain.map.dto.MapRequestDTO;
import com.assu.server.domain.map.dto.MapResponseDTO;
import com.assu.server.domain.map.entity.Location;
import com.assu.server.domain.map.entity.enums.LocationOwnerType;
import com.assu.server.domain.map.repository.MapRepository;
import com.assu.server.domain.partner.entity.Partner;
import com.assu.server.domain.partner.repository.PartnerRepository;
import com.assu.server.domain.partnership.entity.Paper;
import com.assu.server.domain.partnership.entity.PaperContent;
import com.assu.server.domain.partnership.repository.PaperContentRepository;
import com.assu.server.domain.partnership.repository.PaperRepository;
import com.assu.server.domain.store.entity.Store;
import com.assu.server.domain.store.repository.StoreRepository;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.config.KakaoLocalClient;
import com.assu.server.global.exception.exception.DatabaseException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MapServiceImpl implements MapService {

    private final MapRepository mapRepository;
    private final AdminRepository adminRepository;
    private final PartnerRepository partnerRepository;
    private final StoreRepository storeRepository;
    private final KakaoLocalClient kakaoLocalClient;
    private final PaperContentRepository paperContentRepository;
    private final PaperRepository paperRepository;
    private final GeometryFactory geometryFactory;

    @Override
    @Transactional
    public MapResponseDTO.SavePinResponseDTO saveAdminPin() {
//        Long adminId = SecurityUtil.getCurrentId();
        Long adminId = 1L;

        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

        String query = joinAddress(admin.getOfficeAddress(), admin.getDetailAddress());
        if (query.isBlank()) throw new IllegalArgumentException("관리자 주소가 비어 있습니다.");

        var geo = kakaoLocalClient.geocodeByAddress(query);
        Location loc = upsert(LocationOwnerType.ADMIN, admin.getId(), admin.getName(), query, geo.getRoadAddress(), geo.getLat(), geo.getLng());

        return MapConverter.toSavePinResponseDTO(loc);
    }

    @Override
    @Transactional
    public MapResponseDTO.SavePinResponseDTO savePartnerPin() {
        //        Long partnerId = SecurityUtil.getCurrentId();
        Long partnerId = 2L;

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));

        String query = joinAddress(partner.getAddress(), partner.getDetailAddress());
        if (query.isBlank()) throw new IllegalArgumentException("파트너 주소가 비어 있습니다.");

        var geo = kakaoLocalClient.geocodeByAddress(query);
        Location loc = upsert(LocationOwnerType.PARTNER, partner.getId(), partner.getName(), query, geo.getRoadAddress(), geo.getLat(), geo.getLng());

        return MapConverter.toSavePinResponseDTO(loc);
    }

    @Override
    @Transactional
    public MapResponseDTO.SavePinResponseDTO saveStorePin(Long storeId) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STORE));

        String query = joinAddress(store.getAddress(), store.getDetailAddress());
        if (query.isBlank()) throw new IllegalArgumentException("스토어 주소가 비어 있습니다.");

        var geo = kakaoLocalClient.geocodeByAddress(query);
        Location loc = upsert(LocationOwnerType.STORE, store.getId(), store.getName(), query, geo.getRoadAddress(), geo.getLat(), geo.getLng());

        return MapConverter.toSavePinResponseDTO(loc);
    }

    @Override
    public List<MapResponseDTO.PartnerMapResponseDTO> getPartners(MapRequestDTO.ViewOnMapDTO viewport) {
//        Long adminId = SecurityUtil.getCurrentId();
        Long adminId = 1L;
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));
        String wkt = toWKT(viewport);

        List<Location> pins = mapRepository.findAllByCoordinates(LocationOwnerType.PARTNER.name(), wkt);

        return pins.stream().map(pin -> {
            Long partnerId = pin.getOwnerId();
            Partner partner = partnerRepository.findById(partnerId)
                    .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));

            Paper activePaper = paperRepository
                    .findTopByAdmin_IdAndPartner_IdAndIsActivatedOrderByIdDesc(admin.getId(), partnerId, ActivationStatus.ACTIVE)
                    .orElse(null);

            boolean isPartnered = (activePaper != null);
            Long partnershipId = (activePaper != null ? activePaper.getId() : null);
            var start = (activePaper != null ? activePaper.getPartnershipPeriodStart() : null);
            var end = (activePaper != null ? activePaper.getPartnershipPeriodEnd() : null);

            return MapResponseDTO.PartnerMapResponseDTO.builder()
                    .pinId(pin.getId())
                    .partnerId(partnerId)
                    .name(partner != null ? partner.getName() : pin.getName())
                    .address(pin.getRoadAddress() != null ? pin.getRoadAddress() : pin.getAddress())
                    .isPartnered(isPartnered)
                    .partnershipId(partnershipId)
                    .partnershipStartDate(start)
                    .partnershipEndDate(end)
                    .latitude(pin.getLatitude())
                    .longitude(pin.getLongitude())
                    .build();
        }).toList();
    }

    @Override
    public List<MapResponseDTO.AdminMapResponseDTO> getAdmins(MapRequestDTO.ViewOnMapDTO viewport) {
//        Long partnerId = SecurityUtil.getCurrentId();
        Long partnerId = 2L;

        Partner partner = partnerRepository.findById(partnerId)
                .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_PARTNER));
        String wkt = toWKT(viewport);

        List<Location> pins = mapRepository.findAllByCoordinates(LocationOwnerType.ADMIN.name(), wkt);

        return pins.stream().map(pin -> {
            Long adminId = pin.getOwnerId();
            Admin admin = adminRepository.findById(adminId)
                    .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

            Paper activePaper = paperRepository
                    .findTopByAdmin_IdAndPartner_IdAndIsActivatedOrderByIdDesc(adminId, partner.getId(), ActivationStatus.ACTIVE)
                    .orElse(null);

            boolean isPartnered = (activePaper != null);
            Long partnershipId = (activePaper != null ? activePaper.getId() : null);
            var start = (activePaper != null ? activePaper.getPartnershipPeriodStart() : null);
            var end = (activePaper != null ? activePaper.getPartnershipPeriodEnd() : null);

            return MapResponseDTO.AdminMapResponseDTO.builder()
                    .pinId(pin.getId())
                    .adminId(adminId)
                    .name(admin != null ? admin.getName() : pin.getName())
                    .address(pin.getRoadAddress() != null ? pin.getRoadAddress() : pin.getAddress())
                    .isPartnered(isPartnered)
                    .partnershipId(partnershipId)
                    .partnershipStartDate(start)
                    .partnershipEndDate(end)
                    .latitude(pin.getLatitude())
                    .longitude(pin.getLongitude())
                    .build();
        }).toList();
    }

    @Override
    public List<MapResponseDTO.StoreMapResponseDTO> getStores(MapRequestDTO.ViewOnMapDTO viewport) {
        String wkt = toWKT(viewport);

        List<Location> pins = mapRepository.findAllByCoordinates(LocationOwnerType.STORE.name(), wkt);

        return pins.stream().map(pin -> {
            Long storeId = pin.getOwnerId();
            Store store = storeRepository.findById(storeId)
                    .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_STORE));

            boolean hasPartner = store.getPartner() != null;

            PaperContent content = paperContentRepository
                    .findTopByPaperStoreIdOrderByIdDesc(storeId)
                    .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_CONTENT));

            Long adminId = paperRepository.findTopPaperByStoreId(storeId)
                    .map(p -> p.getAdmin() != null ? p.getAdmin().getId() : null)
                    .orElseThrow(() -> new DatabaseException(ErrorStatus.NO_SUCH_ADMIN));

            return MapResponseDTO.StoreMapResponseDTO.builder()
                    .pinId(pin.getId())
                    .storeId(storeId)
                    .adminId(adminId)
                    .name(store.getName())
                    .address(pin.getRoadAddress() != null ? pin.getRoadAddress() : pin.getAddress())
                    .rate(store.getRate())
                    .criterionType(content != null ? content.getCriterionType() : null)
                    .optionType(content != null ? content.getOptionType() : null)
                    .people(content != null ? content.getPeople() : null)
                    .cost(content != null ? content.getCost() : null)
                    .category(content != null ? content.getCategory() : null)
                    .discountRate(content != null ? content.getDiscount() : null)
                    .hasPartner(hasPartner)
                    .latitude(pin.getLatitude())
                    .longitude(pin.getLongitude())
                    .build();
        }).toList();
    }

    @Override
    public List<MapResponseDTO.StoreMapResponseDTO> searchStores(String keyword) {
        List<Store> stores = storeRepository.findByNameContainingIgnoreCaseOrderByIdDesc(keyword);

        Map<Long, Location> locationMap = mapRepository
                .findAllByOwnerTypeAndOwnerIdIn(LocationOwnerType.STORE, stores.stream().map(Store::getId).toList())
                .stream().collect(Collectors.toMap(Location::getOwnerId, Function.identity()));

        List<MapResponseDTO.StoreMapResponseDTO> result =  new ArrayList<>();
        for(Store s : stores) {
            Location pin = locationMap.get(s.getId());

            Paper latest = paperRepository.findTopPaperByStoreId(s.getId()).orElse(null);
            Long adminId = (latest != null && latest.getAdmin() != null) ? latest.getAdmin().getId() : null;

            PaperContent content = paperContentRepository
                    .findTopByPaperStoreIdOrderByIdDesc(s.getId())
                    .orElse(null);

            result.add(MapResponseDTO.StoreMapResponseDTO.builder()
                    .pinId(pin != null ? pin.getId() : null)
                    .storeId(s.getId())
                    .adminId(adminId)
                    .name(s.getName())
                    .address(pin != null
                            ? (pin.getRoadAddress() != null ? pin.getRoadAddress() : pin.getAddress()) : null)
                    .rate(s.getRate())
                    .criterionType(content != null ? content.getCriterionType() : null)
                    .optionType(content != null ? content.getOptionType() : null)
                    .people(content != null ? content.getPeople() : null)
                    .cost(content != null ? content.getCost() : null)
                    .category(content != null ? content.getCategory() : null)
                    .discountRate(content != null ? content.getDiscount() : null)
                    .hasPartner(s.getPartner() != null)
                    .latitude(pin != null ? pin.getLatitude() : null)
                    .longitude(pin != null ? pin.getLongitude() : null)
                    .build());
        }
        return result;
    }

    @Override
    public List<MapResponseDTO.PartnerMapResponseDTO> searchPartner(String keyword) {
//        Long adminId = SecurityUtil.getCurrentId();
        Long adminId = 1L;

        List<Partner> partners = paperRepository
                .findActivePartnersForAdminByKeyword(adminId, ActivationStatus.ACTIVE, keyword);

        Map<Long, Location> locationMap = mapRepository
                .findAllByOwnerTypeAndOwnerIdIn(LocationOwnerType.PARTNER,
                        partners.stream().map(Partner::getId).toList())
                .stream().collect(Collectors.toMap(Location::getOwnerId, Function.identity()));

        List<MapResponseDTO.PartnerMapResponseDTO> result =  new ArrayList<>();
        for(Partner p : partners) {
            Location pin = locationMap.get(p.getId());
            Paper active = paperRepository
                    .findTopByAdmin_IdAndPartner_IdAndIsActivatedOrderByIdDesc(adminId, p.getId(), ActivationStatus.ACTIVE)
                    .orElse(null);

            result.add(MapResponseDTO.PartnerMapResponseDTO.builder()
                            .pinId(pin != null ? pin.getId() : null)
                            .partnerId(p.getId())
                            .name(p.getName())
                            .address(pin != null && pin.getRoadAddress() != null ? pin.getRoadAddress() : (pin != null ? pin.getAddress() : null))
                            .isPartnered(active != null)
                            .partnershipId(active != null ? active.getId() : null)
                            .partnershipStartDate(active != null ? active.getPartnershipPeriodStart() : null)
                            .partnershipEndDate(active != null ? active.getPartnershipPeriodEnd() : null)
                            .latitude(pin != null ? pin.getLatitude() : null)
                            .longitude(pin != null ? pin.getLongitude() : null)
                    .build());
        }
        return result;
    }

    @Override
    public List<MapResponseDTO.AdminMapResponseDTO> searchAdmin(String keyword) {
//        Long partnerId = SecurityUtil.getCurrentId();
        Long partnerId = 2L;

        List<Admin> admins = paperRepository
                .findActiveAdminsForPartnerByKeyword(partnerId, ActivationStatus.ACTIVE, keyword);

        Map<Long, Location> locMap = mapRepository
                .findAllByOwnerTypeAndOwnerIdIn(LocationOwnerType.ADMIN,
                        admins.stream().map(Admin::getId).toList())
                .stream().collect(Collectors.toMap(Location::getOwnerId, Function.identity()));

        List<MapResponseDTO.AdminMapResponseDTO> result = new ArrayList<>();
        for (Admin a : admins) {
            Location pin = locMap.get(a.getId());
            Paper active = paperRepository
                    .findTopByAdmin_IdAndPartner_IdAndIsActivatedOrderByIdDesc(
                            a.getId(), partnerId, ActivationStatus.ACTIVE)
                    .orElse(null);

            result.add(MapResponseDTO.AdminMapResponseDTO.builder()
                    .pinId(pin != null ? pin.getId() : null)
                    .adminId(a.getId())
                    .name(a.getName())
                    .address(pin != null && pin.getRoadAddress() != null ? pin.getRoadAddress()
                            : (pin != null ? pin.getAddress() : null))
                    .isPartnered(active != null)
                    .partnershipId(active != null ? active.getId() : null)
                    .partnershipStartDate(active != null ? active.getPartnershipPeriodStart() : null)
                    .partnershipEndDate(active != null ? active.getPartnershipPeriodEnd() : null)
                    .latitude(pin != null ? pin.getLatitude() : null)
                    .longitude(pin != null ? pin.getLongitude() : null)
                    .build());
        }
        return result;
    }

    private String toWKT(MapRequestDTO.ViewOnMapDTO v) {
        return String.format(
                "POLYGON((%f %f, %f %f, %f %f, %f %f, %f %f))",
                v.getLng1(), v.getLat1(),
                v.getLng2(), v.getLat2(),
                v.getLng3(), v.getLat3(),
                v.getLng4(), v.getLat4(),
                v.getLng1(), v.getLat1()
        );
    }

    private String joinAddress(String addr, String detail) {
        String a = (addr == null) ? "" : addr.trim();
        String d = (detail == null) ? "" : detail.trim();
        return (a + " " + d).trim();
    }

    public Location upsert(LocationOwnerType ownerType, Long ownerId, String name, String plainAddress, String roadAddress, Double lat, Double lng) {

        Location loc = mapRepository.findByOwnerTypeAndOwnerId(ownerType, ownerId)
                .orElseGet(() -> Location.builder().ownerType(ownerType).ownerId(ownerId).build());

        loc.setName(name);
        loc.setAddress(plainAddress);
        loc.setRoadAddress(roadAddress);
        loc.setLatitude(lat);
        loc.setLongitude(lng);

        Point p = geometryFactory.createPoint(new Coordinate(lng, lat));
        loc.setPoint(p);

        return mapRepository.save(loc);
    }

}
