package com.assu.server.domain.map.controller;

import com.assu.server.domain.map.dto.MapRequestDTO;
import com.assu.server.domain.map.dto.MapResponseDTO;
import com.assu.server.domain.map.service.MapService;
import com.assu.server.global.apiPayload.BaseResponse;
import com.assu.server.global.apiPayload.code.status.ErrorStatus;
import com.assu.server.global.apiPayload.code.status.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/map")
public class MapController {

    private final MapService mapService;

    @Operation(
            summary = "관리자 위치 및 정보를 저장하는 API 입니다.",
            description = "로그인된 관리자 프로필의 주소를 사용해 위치를 저장/갱신합니다."
    )
    @PostMapping("/locations/admin")
    public BaseResponse<MapResponseDTO.SavePinResponseDTO> saveAdminPin() {
        return BaseResponse.onSuccess(SuccessStatus._OK, mapService.saveAdminPin());
    }

    @Operation(
            summary = "파트너 위치 및 정보를 저장하는 API 입니다.",
            description = "로그인된 파트너 프로필의 주소를 사용해 위치를 저장/갱신합니다."
    )
    @PostMapping("/locations/partner")
    public BaseResponse<MapResponseDTO.SavePinResponseDTO> savePartnerPin() {
        return BaseResponse.onSuccess(SuccessStatus._OK, mapService.savePartnerPin());
    }

    @Operation(
            summary = "가게 위치 및 정보를 저장하는 API 입니다.",
            description = "storeId로 스토어를 조회하고 그 주소로 위치를 저장/갱신합니다."
    )
    @PostMapping("/locations/store/{storeId}")
    public BaseResponse<MapResponseDTO.SavePinResponseDTO> saveStorePin(
            @PathVariable Long storeId
    ) {
        return BaseResponse.onSuccess(SuccessStatus._OK, mapService.saveStorePin(storeId));
    }

    @Operation(
            summary = "주변 장소를 조회하는 API 입니다.",
            description = "유저의 타입과 공간 인덱싱에 들어갈 좌표 4개를 경도, 위도 순서로 입력해주세요 (type=user -> store 조회 / type=admin -> partner 조회 / type=partner -> admin 조회)"
    )
    @GetMapping("/nearby")
    public BaseResponse<?> getLocations(
            @RequestParam("type") String type,
            @ModelAttribute MapRequestDTO.ViewOnMapDTO viewport
    ) {
        String t = type.trim().toLowerCase();

        return switch (t) {
            case "user" -> BaseResponse.onSuccess(SuccessStatus._OK, mapService.getStores(viewport));
            case "admin" -> BaseResponse.onSuccess(SuccessStatus._OK, mapService.getPartners(viewport));
            case "partner" -> BaseResponse.onSuccess(SuccessStatus._OK, mapService.getAdmins(viewport));
            default -> BaseResponse.onFailure(ErrorStatus._BAD_REQUEST, null);
        };
    }

    @Operation(
            summary = "검색어 기반 장소 조회 API 입니다.",
            description = "유저의 타입과 검색어를 입력해주세요 (type=user → STORE 전체조회 / type=admin → 제휴중인 PARTNER 조회 / type=partner → 제휴중인 ADMIN 조회)"
    )
    @GetMapping("/search")
    public BaseResponse<?> search(
            @RequestParam("type") String type,
            @RequestParam("q") @NotNull String keyword
    ) {
        String t = type.trim().toLowerCase();
        return switch (t) {
            case "user" -> {
                List<MapResponseDTO.StoreMapResponseDTO> list = mapService.searchStores(keyword);
                yield BaseResponse.onSuccess(SuccessStatus._OK, list);
            }
            case "admin" -> {
                List<MapResponseDTO.PartnerMapResponseDTO> list = mapService.searchPartner(keyword);
                yield BaseResponse.onSuccess(SuccessStatus._OK, list);
            }
            case "partner" -> {
                List<MapResponseDTO.AdminMapResponseDTO> list = mapService.searchAdmin(keyword);
                yield BaseResponse.onSuccess(SuccessStatus._OK, list);
            }
            default -> BaseResponse.onFailure(ErrorStatus._BAD_REQUEST, null);
        };
    }

}
