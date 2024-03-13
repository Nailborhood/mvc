package com.nailshop.nailborhood.controller.owner;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.service.owner.OwnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/nailborhood")
public class OwnerController {

    private final OwnerService ownerService;

    // 검색기능이랑 통합
    @GetMapping("/owner/review/{shopId}")
    public String getShopReviewList(Model model,
                                    @PathVariable Long shopId,
                                    @RequestParam(value = "keyword",required = false) String keyword,
                                    @RequestParam(value = "page", defaultValue = "1", required = false) int page,
                                    @RequestParam(value = "size", defaultValue = "10", required = false) int size,
                                    @RequestParam(value = "orderby", defaultValue = "createdAt", required = false) String criteria,
                                    @RequestParam(value = "sort", defaultValue = "DESC", required = false) String sort){
        CommonResponseDto<Object> shopReview = ownerService.getAllReviewListByShopId(keyword, page, size, criteria, sort, shopId);
        model.addAttribute("reviewList", shopReview.getData());

        return "owner/review_manage";
    }
}