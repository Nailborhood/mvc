package com.nailshop.nailborhood.dto.member.response;

import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class MemberListResponseDto {

    private List<MemberInfoDto> memberInfoDtoList;
    private PaginationDto paginationDto;

    @Builder
    public MemberListResponseDto(List<MemberInfoDto> memberInfoDtoList, PaginationDto paginationDto) {
        this.memberInfoDtoList = memberInfoDtoList;
        this.paginationDto = paginationDto;
    }
}
