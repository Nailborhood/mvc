package com.nailshop.nailborhood.controller.artboard;

import com.nailshop.nailborhood.dto.artboard.*;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.service.artboard.*;
import com.nailshop.nailborhood.type.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Controller
@RequiredArgsConstructor
public class ArtController {

    private final ArtRegistrationService artRegistrationService;
    private final ArtUpdateService artUpdateService;
    private final ArtDeleteService artDeleteService;
    private final ArtLikeService artLikeService;
    private final ArtInquiryService artInquiryService;

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 등록", description = "owner API")
    @GetMapping("/owner/artboard/register")
    public String showRegisterArt(Model model,
                                  ArtRegistrationRequestDto artRegistrationRequestDto){

        model.addAttribute("artDto", artRegistrationRequestDto);

        return "artboard/art_registration";
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 등록", description = "owner API")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/register")
    public String registerArt(/*@RequestHeader(AUTH) String accessToken,*/
                                                       @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                                                       @ModelAttribute ArtRegistrationRequestDto artRegistrationRequestDto,
                                                       RedirectAttributes redirectAttributes) {
        try {
            CommonResponseDto<Object> registerArt = artRegistrationService.registerArt(/*accessToken, */multipartFileList, artRegistrationRequestDto);
            ResultDto<Void> resultDto = ResultDto.in(registerArt.getStatus(), registerArt.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/user/artboard/inquiry";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.ART_REGISTRATION_FAIL.getDescription());

            return "artboard/art_registration";
        }
    }
    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 수정", description = "owner API")
    @GetMapping("/owner/artboard/modify/{artRefId}")
    public String showUpdateArt(Model model,
                                @PathVariable Long artRefId){
        CommonResponseDto<Object> artDetail = artInquiryService.inquiryArt(artRefId);
        ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(artDetail.getStatus(), artDetail.getMessage());
        resultDto.setData((ArtDetailResponseDto) artDetail.getData());

        model.addAttribute("result", resultDto);

        return "artboard/art_update";
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 수정", description = "owner API")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/modify/{artRefId}")
    public String updateArtRef(/*@RequestHeader(AUTH) String accessToken,*/
                               @PathVariable Long artRefId,
                               @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                               @ModelAttribute ArtUpdateRequestDto artUpdateRequestDto,
                               RedirectAttributes redirectAttributes){

        try{
            CommonResponseDto<Object> updateArt = artUpdateService.updateArt(/*accessToken, */multipartFileList, artUpdateRequestDto, artRefId);
            ResultDto<Void> resultDto = ResultDto.in(updateArt.getStatus(), updateArt.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/mypage/owner/artboard/manage";
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.ART_UPDATE_FAIL.getDescription());

            return "artboard/art_update";
        }
    }

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 삭제", description = "owner API")
    @DeleteMapping( "/owner/artboard/delete/{artRefId}")
    public ResponseEntity<ResultDto<Void>> deleteArtRef(/*@RequestHeader(AUTH) String accessToken,*/
                                                        @PathVariable Long artRefId){
        CommonResponseDto<Object> deleteArt = artDeleteService.deleteArt(/*accessToken, */artRefId);
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
        boolean error = false;

        try {
            CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArt(page, size, sortBy, category);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
            resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

            model.addAttribute("result", resultDto);
            model.addAttribute("error", error);
        } catch (Exception e) {

            error = true;
            model.addAttribute(error);
        }


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
