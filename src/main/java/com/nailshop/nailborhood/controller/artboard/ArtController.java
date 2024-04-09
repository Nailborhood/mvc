package com.nailshop.nailborhood.controller.artboard;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.dto.artboard.*;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.artboard.*;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    private final CategoryRepository categoryRepository;

    // 아트판 등록(GET)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping("/owner/artboard/register")
    public String showRegisterArt(@AuthenticationPrincipal MemberDetails memberDetails,
                                  Model model){

        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";

        List<Category> categoryList = categoryRepository.findAll();

        model.addAttribute("categories", categoryList);
        model.addAttribute("memberNickname", nicknameSpace);

        return "artboard/art_registration";
    }

    // 아트판 등록(POST)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/register")
    public String registerArt(@AuthenticationPrincipal MemberDetails memberDetails,
                              @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                              @ModelAttribute ArtRegistrationRequestDto artRegistrationRequestDto,
                              RedirectAttributes redirectAttributes) {
        try {
            CommonResponseDto<Object> registerArt = artRegistrationService.registerArt(memberDetails, multipartFileList, artRegistrationRequestDto);
            ResultDto<Void> resultDto = ResultDto.in(registerArt.getStatus(), registerArt.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/artboard/inquiry";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.ART_REGISTRATION_FAIL.getDescription());

            return "artboard/art_registration";
        }
    }

    // 아트판 수정(GET)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @GetMapping("/owner/artboard/modify/{artRefId}")
    public String showUpdateArt(@AuthenticationPrincipal MemberDetails memberDetails,
                                Model model,
                                @PathVariable Long artRefId){
        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";

        CommonResponseDto<Object> artDetail = artInquiryService.inquiryArt(artRefId, memberDetails);
        ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(artDetail.getStatus(), artDetail.getMessage());
        resultDto.setData((ArtDetailResponseDto) artDetail.getData());

        List<Category> categoryList = categoryRepository.findAll();

        model.addAttribute("result", resultDto);
        model.addAttribute("memberNickname", nicknameSpace);
        model.addAttribute("categories", categoryList);

        return "artboard/art_update";
    }

    // 아트판 수정(POST)
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/modify/{artRefId}")
    public String updateArtRef(@AuthenticationPrincipal MemberDetails memberDetails,
                               @PathVariable Long artRefId,
                               @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                               @ModelAttribute ArtUpdateRequestDto artUpdateRequestDto,
                               RedirectAttributes redirectAttributes){

        try{
            CommonResponseDto<Object> updateArt = artUpdateService.updateArt(memberDetails, multipartFileList, artUpdateRequestDto, artRefId);
            ResultDto<Void> resultDto = ResultDto.in(updateArt.getStatus(), updateArt.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/owner/artboard/manage";
        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.ART_UPDATE_FAIL.getDescription());

            return "artboard/art_update";
        }
    }

    // 아트판 삭제
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER')")
    @DeleteMapping( "/owner/artboard/delete/{artRefId}")
    public ResponseEntity<ResultDto<Void>> deleteArtRef(/*@RequestHeader(AUTH) String accessToken,*/
                                                        @PathVariable Long artRefId){
        CommonResponseDto<Object> deleteArt = artDeleteService.deleteArt(/*accessToken, */artRefId);
        ResultDto<Void> resultDto = ResultDto.in(deleteArt.getStatus(), deleteArt.getMessage());

        return ResponseEntity.status(deleteArt.getHttpStatus()).body(resultDto);
    }

    // 아트판 좋아요
//    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_OWNER', 'ROLE_USER')")
    @PostMapping("/artboard/like/{artRefId}")
    public ResponseEntity<ResultDto<ArtLikeResponseDto>> likeArtRef(@AuthenticationPrincipal MemberDetails memberDetails,
                                                                    @PathVariable Long artRefId){
        CommonResponseDto<Object> likeArt = artLikeService.likeArt(memberDetails, artRefId);
        ResultDto<ArtLikeResponseDto> resultDto = ResultDto.in(likeArt.getStatus(), likeArt.getMessage());
        resultDto.setData((ArtLikeResponseDto) likeArt.getData());

        return ResponseEntity.status(likeArt.getHttpStatus()).body(resultDto);
    }

    // 아트판 전체 조회
    @GetMapping("/artboard/inquiry")
    public String inquiryAllArtRef(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                   @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   Model model){
        boolean error = false;

        try {
            CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArt(page, size, sortBy, category, keyword);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
            resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

            List<Category> categoryList = categoryRepository.findAll();

            model.addAttribute("result", resultDto);
            model.addAttribute("error", error);
            model.addAttribute("categories", categoryList);
            model.addAttribute("keyword", keyword);
        } catch (Exception e) {

            error = true;
            model.addAttribute(error);
        }


        return "artboard/art_list";
    }

    // 아트판 전체 조회(카테고리 선택)
    @GetMapping("/artboard/category/inquiry")
    public ResponseEntity<ResultDto<ArtListResponseDto>> inquiryAllArtRefByCategory(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                                                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                                                                    @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                                                                    @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                                                                    @RequestParam(value = "keyword", required = false) String keyword){
        CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArt(page, size, sortBy, category, keyword);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
        resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

        return ResponseEntity.status(inquiryAllArt.getHttpStatus()).body(resultDto);
    }


    // 아트판 상세 조회
    @GetMapping("/artboard/inquiry/{artRefId}")
    public String inquiryArtRef(@AuthenticationPrincipal MemberDetails memberDetails,
                                @PathVariable Long artRefId,
                                Model model){

        String nicknameSpace = (memberDetails != null) ? memberDetails.getMember().getNickname() : "";
        model.addAttribute("memberNickname", nicknameSpace);

        boolean isLoggedIn = memberDetails != null;

        CommonResponseDto<Object> inquiryArt;

        if (isLoggedIn) {
            // 로그인한 경우
            inquiryArt = artInquiryService.inquiryArt(artRefId, memberDetails);
        } else {
            inquiryArt = artInquiryService.inquiryArtForGuest(artRefId);
        }

        ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(inquiryArt.getStatus(), inquiryArt.getMessage());
        resultDto.setData((ArtDetailResponseDto) inquiryArt.getData());

        model.addAttribute("result", resultDto);

        return "artboard/art_detail";
    }
}
