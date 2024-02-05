package com.nailshop.nailborhood.controller.shop;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.ShopModifiactionRequestDto;
import com.nailshop.nailborhood.dto.shop.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.service.shop.admin.ShopRegistrationService;
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
public class ShopController {

    private final ShopRegistrationService shopRegistrationService;
    private final ShopModificationService shopModificationService;

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
}
