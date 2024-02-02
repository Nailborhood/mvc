package com.nailshop.nailborhood.controller.shop;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.shop.ShopRegistrationRequestDto;
import com.nailshop.nailborhood.service.shop.ShopRegistrationService;
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

    @Tag(name ="admin" ,description = "admin API")
    @Operation(summary = "매장 정보 등록" , description = "admin API")
    // 매장 정보 등록
    @PostMapping(consumes = {"multipart/form-data"},value ="/admin/registration")
    public ResponseEntity<ResultDto<Void>> registerShop(@RequestPart(value ="file" )List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data")ShopRegistrationRequestDto shopRegistrationRequestDto){
        CommonResponseDto<Object> commonResponseDto = shopRegistrationService.registerShop(multipartFileList,shopRegistrationRequestDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }
}
