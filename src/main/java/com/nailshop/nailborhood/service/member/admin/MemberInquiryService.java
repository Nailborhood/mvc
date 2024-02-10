package com.nailshop.nailborhood.service.member.admin;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.PaginationDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import com.nailshop.nailborhood.dto.member.response.MemberListResponseDto;
import com.nailshop.nailborhood.exception.NotFoundException;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MemberInquiryService {

    private final CommonService commonService;
    private final MemberRepository memberRepository;

    // 전체 조회
    public CommonResponseDto<Object> inquiryAllMember(int page, int size, String sortBy) {

        // 페이지 설정 및 MemberList get
        PageRequest pageable = PageRequest.of(page - 1, size, Sort.by(sortBy).descending());

        Page<Member> memberPage = memberRepository.findAll(pageable);
        if (memberPage.isEmpty()) throw new NotFoundException(ErrorCode.MEMBER_NOT_FOUND);

        List<Member> memberList = memberPage.getContent();
        List<MemberInfoDto> memberInfoDtoList = new ArrayList<>();

        // MemberInfoDto build
        for (Member member : memberList){

            MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                    .email(member.getEmail())
                    .name(member.getName())
                    .birthday(member.getBirthday())
                    .phoneNum(member.getPhoneNum())
                    .gender(member.getGender())
                    .address(member.getAddress())
                    .nickname(member.getNickname())
                    .profileImg(member.getProfileImg())
                    .isDeleted(member.isDeleted())
                    .createdAt(member.getCreatedAt())
                    .build();

            memberInfoDtoList.add(memberInfoDto);
        }

        // PaginationDto build
        PaginationDto paginationDto = PaginationDto.builder()
                .totalPages(memberPage.getTotalPages())
                .totalElements(memberPage.getTotalElements())
                .pageNo(memberPage.getNumber())
                .isLastPage(memberPage.isLast())
                .build();

        // MemberListResponseDto build
        MemberListResponseDto memberListResponseDto = MemberListResponseDto.builder()
                .memberInfoDtoList(memberInfoDtoList)
                .paginationDto(paginationDto)
                .build();

        return commonService.successResponse(SuccessCode.MEMBER_ALL_INQUIRY_SUCCESS.getDescription(), HttpStatus.OK, memberListResponseDto);
    }
}
