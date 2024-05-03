package com.nailshop.nailborhood.controller.artboard;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.artboard.*;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.alarm.AlarmService;
import com.nailshop.nailborhood.service.artboard.*;
import com.nailshop.nailborhood.service.member.MemberService;
import com.nailshop.nailborhood.type.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
@RequiredArgsConstructor
public class ArtController {

    private final ArtRegistrationService artRegistrationService;
    private final ArtUpdateService artUpdateService;
    private final ArtDeleteService artDeleteService;
    private final ArtLikeService artLikeService;
    private final ArtInquiryService artInquiryService;
    private final CategoryRepository categoryRepository;
    private final AlarmService alarmService;
    private final MemberService memberService;

    // 아트판 등록(GET)
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/owner/artboard/register")
    public String showRegisterArt(Authentication authentication,
                                  @AuthenticationPrincipal MemberDetails memberDetails,
                                  Model model){

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        List<Category> categoryList = categoryRepository.findAll();

        model.addAttribute("categories", categoryList);

        return "artboard/art_registration";
    }

    // 아트판 등록(POST)
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/register")
    public String registerArt(Authentication authentication,
                              @AuthenticationPrincipal MemberDetails memberDetails,
                              @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                              @ModelAttribute ArtRegistrationRequestDto artRegistrationRequestDto,
                              RedirectAttributes redirectAttributes) {
        try {
            SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
            Long artRefId = artRegistrationService.registerArt(sessionDto.getId(), multipartFileList, artRegistrationRequestDto);

//            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());

            return "redirect:/artboard/inquiry/" + artRefId;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.ART_REGISTRATION_FAIL.getDescription());

            return "artboard/art_registration";
        }
    }

    // 아트판 수정(GET)
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/owner/artboard/modify/{artRefId}")
    public String showUpdateArt(@AuthenticationPrincipal MemberDetails memberDetails,
                                Authentication authentication,
                                Model model,
                                @PathVariable Long artRefId){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        model.addAttribute("sessionDto", sessionDto);

        CommonResponseDto<Object> artDetail = artInquiryService.inquiryArt(artRefId, sessionDto.getId());
        ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(artDetail.getStatus(), artDetail.getMessage());
        resultDto.setData((ArtDetailResponseDto) artDetail.getData());

        List<Category> categoryList = categoryRepository.findAll();

        model.addAttribute("result", resultDto);
        model.addAttribute("categories", categoryList);

        return "artboard/art_update";
    }

    // 아트판 수정(POST)
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @PostMapping(consumes = {"multipart/form-data"}, value = "/owner/artboard/modify/{artRefId}")
    public String updateArtRef(Authentication authentication,
                               @AuthenticationPrincipal MemberDetails memberDetails,
                               @PathVariable Long artRefId,
                               @RequestPart(value = "file") List<MultipartFile> multipartFileList,
                               @ModelAttribute ArtUpdateRequestDto artUpdateRequestDto,
                               RedirectAttributes redirectAttributes){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        try{
            CommonResponseDto<Object> updateArt = artUpdateService.updateArt(sessionDto.getId(), multipartFileList, artUpdateRequestDto, artRefId);
            ResultDto<Void> resultDto = ResultDto.in(updateArt.getStatus(), updateArt.getMessage());

            redirectAttributes.addFlashAttribute("successMessage", resultDto.getMessage());
            redirectAttributes.addAttribute("artRefId", artRefId);

            return "redirect:/artboard/inquiry/" + artRefId;

        } catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", ErrorCode.ART_UPDATE_FAIL.getDescription());

            return "artboard/art_update";
        }
    }

    // 아트판 삭제
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN')")
    @DeleteMapping( "/owner/artboard/delete/{artRefId}")
    public ResponseEntity<ResultDto<Void>> deleteArtRef(Authentication authentication,
                                                        @AuthenticationPrincipal MemberDetails memberDetails,
                                                        @PathVariable Long artRefId){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);

        CommonResponseDto<Object> deleteArt = artDeleteService.deleteArt(sessionDto.getId(), artRefId);
        ResultDto<Void> resultDto = ResultDto.in(deleteArt.getStatus(), deleteArt.getMessage());

        return ResponseEntity.status(deleteArt.getHttpStatus()).body(resultDto);
    }

    // 아트판 좋아요
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/artboard/like/{artRefId}")
    public ResponseEntity<ResultDto<ArtLikeResponseDto>> likeArtRef(Authentication authentication,
                                                                    @AuthenticationPrincipal MemberDetails memberDetails,
                                                                    @PathVariable Long artRefId){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        CommonResponseDto<Object> likeArt = artLikeService.likeArt(sessionDto.getId(), artRefId);
        ResultDto<ArtLikeResponseDto> resultDto = ResultDto.in(likeArt.getStatus(), likeArt.getMessage());
        resultDto.setData((ArtLikeResponseDto) likeArt.getData());

        return ResponseEntity.status(likeArt.getHttpStatus()).body(resultDto);
    }

    // 아트판 북마크
    @PreAuthorize("hasRole('ROLE_OWNER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
    @PostMapping("/artboard/bookmark/{artRefId}")
    public ResponseEntity<ResultDto<ArtBookMarkResponseDto>> bookMarkArtRef(Authentication authentication,
                                                                    @AuthenticationPrincipal MemberDetails memberDetails,
                                                                    @PathVariable Long artRefId){
        SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
        CommonResponseDto<Object> bookMarkArt = artLikeService.bookMarkArt(sessionDto.getId(), artRefId);
        ResultDto<ArtBookMarkResponseDto> resultDto = ResultDto.in(bookMarkArt.getStatus(), bookMarkArt.getMessage());
        resultDto.setData((ArtBookMarkResponseDto) bookMarkArt.getData());

        return ResponseEntity.status(bookMarkArt.getHttpStatus()).body(resultDto);
    }

    // 아트판 전체 조회
    @GetMapping("/artboard/inquiry")
    public String inquiryAllArtRef(@RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "16", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "likeCount", required = false) String sortBy,
                                   @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                   @RequestParam(value = "keyword", required = false) String keyword,
                                   Model model,
                                   @AuthenticationPrincipal MemberDetails memberDetails,
                                   Authentication authentication){
        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        boolean error = false;
        try {
            CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArt(page, size, sortBy, category, keyword);
            ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
            resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

            List<Map<String, String>> criteriaOptions = artInquiryService.createCriteriaOptions();
            List<Category> categoryList = categoryRepository.findAll();

            model.addAttribute("result", resultDto);
            model.addAttribute("error", error);
            model.addAttribute("sortBy", sortBy);
            model.addAttribute("size", size);
            model.addAttribute("criteriaOptions", criteriaOptions);
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
                                                                                    @RequestParam(value = "size", defaultValue = "20", required = false) int size,
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
                                Model model,
                                Authentication authentication){

        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication, memberDetails);
            model.addAttribute("sessionDto", sessionDto);
            CommonResponseDto<Object> inquiryArt = artInquiryService.inquiryArt(artRefId, sessionDto.getId());
            ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(inquiryArt.getStatus(), inquiryArt.getMessage());
            resultDto.setData((ArtDetailResponseDto) inquiryArt.getData());

            // 알람
            Member receiver = alarmService.getOwnerInfo(resultDto.getData()
                    .getShopId());

            model.addAttribute("result", resultDto);
            model.addAttribute("receiver",receiver);
        } else {
            CommonResponseDto<Object> inquiryArt = artInquiryService.inquiryArtForGuest(artRefId);
            ResultDto<ArtDetailResponseDto> resultDto = ResultDto.in(inquiryArt.getStatus(), inquiryArt.getMessage());
            resultDto.setData((ArtDetailResponseDto) inquiryArt.getData());
            Member receiver = alarmService.getOwnerInfo(resultDto.getData()
                    .getShopId());

            model.addAttribute("result", resultDto);
            model.addAttribute("sessionDto", null);
            model.addAttribute("receiver",receiver);
        }

        return "artboard/art_detail";
    }
}
