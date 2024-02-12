package com.nailshop.nailborhood.service.shop.admin;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.exception.BadRequestException;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.ReviewReportStatus;
import com.nailshop.nailborhood.type.ShopStatus;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShopStatusChangeService {
    private final ShopRepository shopRepository;
    private final CommonService commonService;

    @Transactional
    // 매장 상태 변경
    public CommonResponseDto<Object> changeShopStatus(Long shopId, String status) {

        ShopStatus changeStatus = null;
        SuccessCode successCode = null;


        if (status.equals("before")) {
            // 매장 오픈 전
            changeStatus = ShopStatus.BEFORE_OPEN;
            successCode = SuccessCode.SHOP_STATUS_BEFORE_OPEN;
        } else if (status.equals("closed")) {
            // 매장 종료
            changeStatus = ShopStatus.CLOSED;
            successCode = SuccessCode.SHOP_STATUS_CLOSED;
        } else {
            // 매장 오픈
            changeStatus = ShopStatus.OPEN;
            successCode = SuccessCode.SHOP_STATUS_OPEN;
        }

        if (changeStatus == null) {
            throw new BadRequestException(ErrorCode.SHOPSTATUS_NOT_FOUND);
        }
        shopRepository.updateShopStatusByShopId(shopId, changeStatus);

        return commonService.successResponse(successCode.getDescription(), HttpStatus.OK, null);
    }
}


