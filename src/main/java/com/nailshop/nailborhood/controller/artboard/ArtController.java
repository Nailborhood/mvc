package com.nailshop.nailborhood.controller.artboard;

import com.nailshop.nailborhood.dto.artboard.ArtRegistrationDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.service.artboard.ArtRegistrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/nailshop"))
public class ArtController {

    private final ArtRegistrationService artRegistrationService;

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 등록", description = "owner API")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/resister")
    public ResponseEntity<ResultDto<Void>> registerArt(@RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                       @RequestPart(value = "data")ArtRegistrationDto artRegistrationDto){
        CommonResponseDto<Object> commonResponseDto = artRegistrationService.registerArt(multipartFileList, artRegistrationDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }
}
