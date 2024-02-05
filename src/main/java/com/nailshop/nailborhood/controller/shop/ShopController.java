package com.nailshop.nailborhood.controller.shop;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.response.ShopListResponseDto;
import com.nailshop.nailborhood.service.shop.ShopListLookupHomeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailshop")
public class ShopController {

    private final ShopListLookupHomeService shopListLookupHomeService;

    @Tag(name = "Home", description = "Home API")
    @Operation(summary = "매장 전체 조회", description = "Home API")
    // 전체 매장 조회
    @GetMapping(value = "/shopList")
    public ResponseEntity<ResultDto<ShopListResponseDto>> getAllShops(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                      @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                      @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                      @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> allShopsList = shopListLookupHomeService.getShopList(page, size, sort, criteria);
        ResultDto<ShopListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
        resultDto.setData((ShopListResponseDto) allShopsList.getData());

        return ResponseEntity.status(allShopsList.getHttpStatus())
                             .body(resultDto);
    }

    @Tag(name = "Home", description = "Home API")
    @Operation(summary = "매장 전체 조회", description = "Home API")
    // 전체 매장 조회
    @GetMapping(value = "/shopList/{dongId}")
    public ResponseEntity<ResultDto<ShopListResponseDto>> getAllShopsbyDong(@PathVariable Long dongId,
                                                                            @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                            @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                            @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                                                            @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort) {
        CommonResponseDto<Object> allShopsList = shopListLookupHomeService.getShopListbyDong( page, size, sort, criteria, dongId);
        ResultDto<ShopListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
        resultDto.setData((ShopListResponseDto) allShopsList.getData());

        return ResponseEntity.status(allShopsList.getHttpStatus())
                             .body(resultDto);
    }
}
