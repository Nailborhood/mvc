package com.nailshop.nailborhood.controller.mypage;

import com.nailshop.nailborhood.dto.artboard.ArtListResponseDto;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.service.artboard.ArtInquiryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@RequiredArgsConstructor
@Controller
@RequestMapping("/mypage")
public class OwnerController {

    private final ArtInquiryService artInquiryService;

    @Tag(name = "owner", description = "owner API")
    @Operation(summary = "아트판 관리", description = "owner API")
    @GetMapping("/owner/artboard/manage")
    public String inquiryAllArtRef(/*@RequestHeader(AUTH) String accessToken,*/
                                   @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                   @RequestParam(value = "size", defaultValue = "5", required = false) int size,
                                   @RequestParam(value = "sortBy", defaultValue = "updatedAt", required = false) String sortBy,
                                   @RequestParam(value = "category", defaultValue = "", required = false) String category,
                                   Model model){
        CommonResponseDto<Object> inquiryAllArt = artInquiryService.inquiryAllArtByShopId(/*accessToken, */page, size, sortBy, category);
        ResultDto<ArtListResponseDto> resultDto = ResultDto.in(inquiryAllArt.getStatus(), inquiryAllArt.getMessage());
        resultDto.setData((ArtListResponseDto) inquiryAllArt.getData());

        model.addAttribute("result", resultDto);
        model.addAttribute("sortBy", sortBy);
        model.addAttribute("size", size);

        return "owner/art_manage";
    }
}
