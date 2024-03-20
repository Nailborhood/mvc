package com.nailshop.nailborhood.service.review;

import com.nailshop.nailborhood.domain.category.Category;
import com.nailshop.nailborhood.domain.category.CategoryReview;
import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.domain.review.ReviewImg;
import com.nailshop.nailborhood.domain.shop.Shop;
import com.nailshop.nailborhood.domain.shop.ShopImg;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.review.request.ReviewRegistrationRequestDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.category.CategoryRepository;
import com.nailshop.nailborhood.repository.category.CategoryReviewRepository;
import com.nailshop.nailborhood.repository.member.CustomerRepository;
import com.nailshop.nailborhood.repository.review.ReviewImgRepository;
import com.nailshop.nailborhood.repository.review.ReviewRepository;
import com.nailshop.nailborhood.repository.shop.ShopRepository;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewRegistrationService {
    private final TokenProvider tokenProvider;
    private final ReviewRepository reviewRepository;
    private final ShopRepository shopRepository;
    private final CommonService commonService;
    private final S3UploadService s3UploadService;
    private final ReviewImgRepository reviewImgRepository;
    private final CategoryRepository categoryRepository;
    private final CategoryReviewRepository categoryReviewRepository;
    private final CustomerRepository customerRepository;


    @Transactional
    // 리뷰 등록
    public CommonResponseDto<Object> registerReview(Long shopId, String accessToken, List<MultipartFile> multipartFileList, ReviewRegistrationRequestDto reviewRegistrationRequestDto) {

        // token 에서 memberId 가져오기
        Long memberId = tokenProvider.getUserId(accessToken);
        Customer customer = customerRepository.findByMemberId(memberId);

        // 매장 존재 여부
        Shop shop = shopRepository.findByShopIdAndIsDeleted(shopId)
                                  .orElseThrow(() -> new NotFoundException(ErrorCode.SHOP_NOT_FOUND));

        // 리뷰 세부정보 등록
        Review review = Review.builder()
                              .contents(reviewRegistrationRequestDto.getContents())
                              .isDeleted(false)
                              .rate(reviewRegistrationRequestDto.getRate())
                              .shop(shop)
                              .likeCnt(0L)
                              .customer(customer)
                              .build();

        review = reviewRepository.save(review);

        // 리뷰 사진 등록
        saveReviewImg(multipartFileList, review);

        // CategoryReview 저장
        for (Long categoryId : reviewRegistrationRequestDto.getCategoryListId()) {

            Category category = categoryRepository.findById(categoryId)
                                                  .orElseThrow(() -> new NotFoundException(ErrorCode.CATEGORY_NOT_FOUND));

            CategoryReview categoryReview = CategoryReview.builder()
                                                          .category(category)
                                                          .review(review)
                                                          .build();

            categoryReviewRepository.save(categoryReview);
        }

        // 리뷰 등록 시 매장 별점 평균 변경
        updateShopRateAvg(shop);

        // 리뷰 등록 시 리뷰 개수 변경
        shopRepository.updateReviewCntIncreaseByShopId(shopId);

        return commonService.successResponse(SuccessCode.REVIEW_REGISTRATION_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    // 이미지 저장
    private void saveReviewImg(List<MultipartFile> multipartFileList, Review review) {
        // s3에 이미지 업로드
        List<String> reviewImgUrlList = s3UploadService.reviewImgUpload(multipartFileList);

        // 이미지 번호 1번 부터 시작
        Integer imgNum = 1;

        for (String imgPath : reviewImgUrlList) {
            ReviewImg reviewImg = ReviewImg.builder()
                                           .imgPath(imgPath)
                                           .imgNum(imgNum)
                                           .isDeleted(false)
                                           .review(review)
                                           .build();
            reviewImgRepository.save(reviewImg);

            imgNum++;
        }
    }

    private void updateShopRateAvg(Shop shop) {
        Long shopId = shop.getShopId();
        List<Review> reviews = reviewRepository.findAllByShopIdAndIsDeleted(shopId);

        double totalRate = reviews.stream()
                                  .mapToInt(Review::getRate)
                                  .sum();
        if(totalRate != 0 ) {
            String rateAvgStr = String.format("%.1f", totalRate / reviews.size());
            double rateAvg = Double.parseDouble(rateAvgStr);
            shopRepository.updateRateAvgByShopId(rateAvg,shopId);
        }else {
            shopRepository.updateRateAvgByShopId(0,shopId);
        }

    }


}
