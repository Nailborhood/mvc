package com.nailshop.nailborhood.controller.artboard;

import com.nailshop.nailborhood.dto.artboard.ArtRegistrationDto;
import com.nailshop.nailborhood.dto.artboard.ArtUpdateDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.service.artboard.ArtDeleteService;
import com.nailshop.nailborhood.service.artboard.ArtRegistrationService;
import com.nailshop.nailborhood.service.artboard.ArtUpdateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(("/nailshop"))
public class ArtController {

    private final ArtRegistrationService artRegistrationService;
    private final ArtUpdateService artUpdateService;
    private final ArtDeleteService artDeleteService;

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 등록", description = "owner API")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/resister")
    public ResponseEntity<ResultDto<Void>> registerArt(@RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                       @RequestPart(value = "data") ArtRegistrationDto artRegistrationDto){
        CommonResponseDto<Object> commonResponseDto = artRegistrationService.registerArt(multipartFileList, artRegistrationDto);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 수정", description = "owner API")
    @PutMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/modify/{artRefId}")
    public ResponseEntity<ResultDto<Void>> updateArtRef(@PathVariable Long artRefId,
                                                        @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data") ArtUpdateDto artUpdateDto){
        CommonResponseDto<Object> commonResponseDto = artUpdateService.updateArt(multipartFileList, artUpdateDto, artRefId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 삭제", description = "owner API")
    @DeleteMapping(consumes = {"multipart/form-data"}, value = "owner/artboard/delete/{artRefId}")
    public ResponseEntity<ResultDto<Void>> deleteArtRef(@PathVariable Long artRefId,
                                                        @RequestPart(value = "file") List<MultipartFile> multipartFileList){
        CommonResponseDto<Object> commonResponseDto = artDeleteService.deleteArt(multipartFileList, artRefId);
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }
}
