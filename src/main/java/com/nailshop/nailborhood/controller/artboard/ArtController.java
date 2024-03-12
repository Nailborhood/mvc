package com.nailshop.nailborhood.controller.artboard;

import com.nailshop.nailborhood.dto.artboard.*;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.service.artboard.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
@RequestMapping("/nailborhood")
public class ArtController {

    private final ArtRegistrationService artRegistrationService;
    private final ArtUpdateService artUpdateService;
    private final ArtDeleteService artDeleteService;
    private final ArtLikeService artLikeService;
    private final ArtInquiryService artInquiryService;

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 등록", description = "owner API")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/register")
    public ResponseEntity<ResultDto<Void>> registerArt(/*@RequestHeader(AUTH) String accessToken,*/
                                                       @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                       @RequestPart(value = "data") ArtRegistrationRequestDto artRegistrationRequestDto){
        CommonResponseDto<Object> registerArt = artRegistrationService.registerArt(/*accessToken, */multipartFileList, artRegistrationRequestDto);
        ResultDto<Void> resultDto = ResultDto.in(registerArt.getStatus(), registerArt.getMessage());

        return ResponseEntity.status(registerArt.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 수정", description = "owner API")
    @PutMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/modify/{artRefId}")
    public ResponseEntity<ResultDto<Void>> updateArtRef(@RequestHeader(AUTH) String accessToken,
                                                        @PathVariable Long artRefId,
                                                        @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                        @RequestPart(value = "data") ArtUpdateRequestDto artUpdateRequestDto){
        CommonResponseDto<Object> updateArt = artUpdateService.updateArt(accessToken, multipartFileList, artUpdateRequestDto, artRefId);
        ResultDto<Void> resultDto = ResultDto.in(updateArt.getStatus(), updateArt.getMessage());

        return ResponseEntity.status(updateArt.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 삭제", description = "owner API")
    @DeleteMapping( "/owner/artboard/delete/{artRefId}")
    public ResponseEntity<ResultDto<Void>> deleteArtRef(@RequestHeader(AUTH) String accessToken,
                                                        @PathVariable Long artRefId){
        CommonResponseDto<Object> deleteArt = artDeleteService.deleteArt(accessToken, artRefId);
        ResultDto<Void> resultDto = ResultDto.in(deleteArt.getStatus(), deleteArt.getMessage());

        return ResponseEntity.status(deleteArt.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "user", description = "user API")
    @Operation(summary = "아트판 좋아요", description = "user API")
    @PostMapping("/user/artboard/like/{artRefId}")
    public ResponseEntity<ResultDto<ArtLikeResponseDto>> likeArtRef(@RequestHeader(AUTH) String accessToken,
                                                                    @PathVariable Long artRefId){
        CommonResponseDto<Object> likeArt = artLikeService.likeArt(accessToken, artRefId);
        ResultDto<ArtLikeResponseDto> resultDto = ResultDto.in(likeArt.getStatus(), likeArt.getMessage());
        resultDto.setData((ArtLikeResponseDto) likeArt.getData());

        return ResponseEntity.status(likeArt.getHttpStatus()).body(resultDto);
    }

    @Tag(name = "user", description = "user API")
    @Operation(summary = "아트판 전체 조회", description = "user API")
    @GetMapping("/user/artboard/inquiry")
    public String inquiryAllArtRef(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                   @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                   Model model){
        CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArt(page, size, sortBy, category);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
        resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

        model.addAttribute("result", resultDto);

        return "artboard/art_list";
    }

    @Tag(name = "user", description = "user API")
    @Operation(summary = "아트판 상세 조회", description = "user API")
    @GetMapping("/user/artboard/inquiry/{artRefId}")
    public String inquiryArtRef(@PathVariable Long artRefId,
                                Model model){
        CommonResponseDto<Object> inquiryArt = artInquiryService.inquiryArt(artRefId);
        ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(inquiryArt.getStatus(), inquiryArt.getMessage());
        resultDto.setData((ArtDetailResponseDto) inquiryArt.getData());

        model.addAttribute("result", resultDto);

        return "artboard/art_detail";
    }
}
