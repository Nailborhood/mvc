package com.nailshop.nailborhood.dto.mypage;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.review.response.ReviewResponseDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MyFavoriteListResponseDto {

    private List<FavoriteShopDetailDto> favoriteShopDetailDtoList;
    private PaginationDto paginationDto;

    @Builder
    public MyFavoriteListResponseDto(List<FavoriteShopDetailDto> favoriteShopDetailDtoList, PaginationDto paginationDto) {
        this.favoriteShopDetailDtoList = favoriteShopDetailDtoList;
        this.paginationDto = paginationDto;
    }
}
