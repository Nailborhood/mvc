package com.nailshop.nailborhood.controller.shop.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.example.ExampleDto;
import com.nailshop.nailborhood.dto.shop.request.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.dto.shop.request.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.dto.shop.response.AllShopsListResponseDto;
import com.nailshop.nailborhood.service.shop.admin.AllShopsLookupAdminService;
import com.nailshop.nailborhood.service.shop.admin.ShopDeleteService;
import com.nailshop.nailborhood.service.shop.admin.ShopRegistrationService;
import com.nailshop.nailborhood.service.shop.admin.ShopStatusChangeService;
import com.nailshop.nailborhood.service.shop.owner.ShopModificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/nailshop")
public class AdminShopController {

    private final ShopRegistrationService shopRegistrationService;
    private final ShopModificationService shopModificationService;
    private final AllShopsLookupAdminService allShopsLookupAdminService;
    private final ShopDeleteService shopDeleteService;
    private final ShopStatusChangeService shopStatusChangeService;

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 정보 등록", description = "admin API")
    // 매장 정보 등록
    @PostMapping(consumes = {"multipart/form-data"}, value = "/admin/registration")
    public ResponseEntity<ResultDto<Void>> registerShop(@RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data") ShopRegistrationRequestDto shopRegistrationRequestDto) {
        CommonResponseDto<Object> commonResponseDto = shopRegistrationService.registerShop(multipartFileList, shopRegistrationRequestDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "매장 정보 수정", description = "owner API")
    // 매장 정보 수정
    @PutMapping(consumes = {"multipart/form-data"}, value = "/owner/update/{shopId}")
    public ResponseEntity<ResultDto<Void>> updateShop(@PathVariable Long shopId,
                                                      @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                      @RequestPart(value = "data") ShopModifiactionRequestDto shopModifiactionRequestDto) {
        CommonResponseDto<Object> commonResponseDto = shopModificationService.updateShop(shopId, multipartFileList, shopModifiactionRequestDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }
    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 전체 조회", description = "admin API")
    // 전체 매장 조회
    @GetMapping(value = "/admin/shopList")
    public ResponseEntity<ResultDto<AllShopsListResponseDto>> getAllShops(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                          @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                         @RequestParam(value = "orderby",defaultValue = "createdAt",required = false) String criteria,
                                                                          @RequestParam(value = "sort", defaultValue = "DESC", required = false)String sort){
        CommonResponseDto<Object> allShopsList = allShopsLookupAdminService.getAllShops(page,size,criteria,sort);
        ResultDto<AllShopsListResponseDto> resultDto = ResultDto.in(allShopsList.getStatus(), allShopsList.getMessage());
        resultDto.setData((AllShopsListResponseDto) allShopsList.getData());

        return ResponseEntity.status(allShopsList.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 삭제", description = "admin API")
    // 매장 정보 등록
    @DeleteMapping(  "/admin/deleteShop/{shopId}")
    public ResponseEntity<ResultDto<Void>> deleteShop(@PathVariable Long shopId) {
        CommonResponseDto<Object> commonResponseDto = shopDeleteService.deleteShop(shopId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }

    @Tag(name = "admin", description = "admin API")
    @Operation(summary = "매장 상태 변경 ", description = "admin API")
    // 매장 상태 변경
    @PutMapping( "/admin/shopStatus/{reportId}")
    public ResponseEntity<ResultDto<Void>> changeReviewReportStatus(@PathVariable Long shopId,
                                                                    @RequestParam(value = "status") String status ) {
        CommonResponseDto<Object> commonResponseDto = shopStatusChangeService.changeShopStatus(shopId, status);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus())
                             .body(resultDto);
    }
}
