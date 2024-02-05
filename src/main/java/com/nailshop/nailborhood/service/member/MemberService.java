package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.member.CheckDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import java.util.Optional;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;

    private final CommonService commonService;

    public CommonResponseDto<Object> checkEmailIsAvailable(CheckDto checkDto) {
        Optional<Member> memberOptional = memberRepository.findByEmail(checkDto.getCheck());
        boolean exist = memberOptional.isPresent();
        checkDto.setExist(exist);
        if(exist) return commonService.successResponse(SuccessCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else return commonService.successResponse(SuccessCode.EMAIL_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

}
