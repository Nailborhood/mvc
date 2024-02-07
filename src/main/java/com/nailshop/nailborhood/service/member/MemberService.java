package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Login;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.member.CheckDto;
import com.nailshop.nailborhood.dto.member.LoginDto;
import com.nailshop.nailborhood.dto.member.MemberInfoDto;
import com.nailshop.nailborhood.dto.member.SignUpDto;
import com.nailshop.nailborhood.repository.member.LoginRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.security.dto.GeneratedToken;
import com.nailshop.nailborhood.security.dto.TokenResponseDto;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final LoginRepository loginRepository;

    private final CommonService commonService;
    private final TokenProvider tokenProvider;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public CommonResponseDto<Object> checkEmailIsAvailable(CheckDto checkDto) {
        boolean exist = findByEmail(checkDto.getCheck());
        checkDto.setExist(exist);
        if(exist) return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else return commonService.successResponse(SuccessCode.EMAIL_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

    public CommonResponseDto<Object> checkNicknameIsAvailable(CheckDto checkDto) {
        boolean exist = findByNickname(checkDto.getCheck());
        checkDto.setExist(exist);
        if(exist) return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else return commonService.successResponse(SuccessCode.NICKNAME_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

    public CommonResponseDto<Object> checkPhoneNumIsAvailable(CheckDto checkDto) {
        boolean exist = findByPhoneNum(checkDto.getCheck());
        checkDto.setExist(exist);
        if(exist) return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else return commonService.successResponse(SuccessCode.PHONENUM_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

//    public CommonResponseDto<Object> checkPasswordMatchesWithRules(CheckDto checkDto) {
//        boolean matches = matchWithPasswordPattern(checkDto.getCheck());
//        checkDto.setExist(matches);
//        if(matches) return commonService.successResponse(ErrorCode.PASSWORD_NOT_MATCH_WITH_PATTERN.getDescription(), HttpStatus.OK,checkDto);
//        else return commonService.successResponse(SuccessCode)
//
//    }


    public CommonResponseDto<Object> signUp(SignUpDto signUpDto) {
        if(findByEmail(signUpDto.getEmail())) return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else if (findByNickname(signUpDto.getNickname())) return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else if (findByPhoneNum(signUpDto.getPhoneNum())) return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else if(!matchWithPasswordPattern(signUpDto.getPassword())) return commonService.errorResponse(ErrorCode.PASSWORD_NOT_MATCH_WITH_PATTERN.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else {
            String encPassword = bCryptPasswordEncoder.encode(signUpDto.getPassword());
            Member member = Member.builder()
                    .email(signUpDto.getEmail())
                    .password(encPassword)
                    .nickname(signUpDto.getNickname())
                    .address(signUpDto.getAddress())
                    .name(signUpDto.getName())
                    .phoneNum(signUpDto.getPhoneNum())
                    .gender(signUpDto.getGender())
                    .birthday(signUpDto.getBirthday())
                    .profileImg("defaultImage")
                    .role(Role.USER)
                    .provider("Nail")
                    .isDeleted(false)
                    .build();
            try {
                memberRepository.save(member);
                return commonService.successResponse(SuccessCode.SIGNUP_SUCCESS.getDescription(), HttpStatus.OK, null);
            } catch (Exception e) {
                return commonService.errorResponse(ErrorCode.SIGNUP_FAIL.getDescription(), HttpStatus.BAD_GATEWAY, signUpDto);
            }
        }
    }

    public boolean findByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        return memberOptional.isPresent();
    }

    public boolean findByNickname(String nickname) {
        Optional<Member> memberOptional = memberRepository.findByNickname(nickname);
        return memberOptional.isPresent();
    }

    public boolean findByPhoneNum(String phoneNum) {
        Optional<Member> memberOptional = memberRepository.findByPhoneNum(phoneNum);
        return memberOptional.isPresent();
    }


    public boolean matchWithPasswordPattern(String password) {
        String passwordPattern = "^[A-Za-z0-9]{8,20}$";
        return Pattern.matches(passwordPattern, password);
    }

    @Transactional
    public CommonResponseDto<Object> memberLogin(LoginDto loginDto) {
        if(!findByEmail(loginDto.getEmail())) return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED,null);
        else if (findByEmail(loginDto.getEmail()) && passwordCheck(loginDto.getEmail(), loginDto.getPassword())) {
            Member member = memberRepository.findByEmail(loginDto.getEmail()).get();
            if(member.isDeleted()) return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED,null);
            else {
                // 토큰 발급
                GeneratedToken generatedToken = tokenProvider.generateToken(member);

                Optional<Login> optionalLogin = loginRepository.findByMember_MemberId(member.getMemberId());
                Login login;
                if(optionalLogin.isEmpty()) {
                    login = Login.builder()
                            .refreshToken(generatedToken.getRefreshToken())
                            .member(member)
                            .build();
                } else {
                    login = optionalLogin.get();
                    login.updateToken(generatedToken.getRefreshToken());
                }
                loginRepository.save(login);

                TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                        .memberId(member.getMemberId())
                        .accessToken(generatedToken.getAccessToken())
                        .accessTokenExpireTime(generatedToken.getAccessTokenExpireTime())
                        .build();


                return commonService.successResponse(SuccessCode.LOGIN_SUCCESS.getDescription(), HttpStatus.OK,tokenResponseDto);
            }
        } else {
            return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED,null);
        }
    }

//    @Transactional
//    public CommonResponseDto<Object> renewToken(String refreshToken) {
//        // 유저 get
//        Login login = loginRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new NotFoundException(ErrorCode.EXAMPLE_EXCEPTION.getDescription()));
//
//        Member member = login.getMember();
//
//        GeneratedToken generatedToken = tokenProvider.generateToken(member);
//
//        login.updateToken(generatedToken.getRefreshToken());
//
//        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
//                .accessToken(generatedToken.getAccessToken())
//                .accessTokenExpireTime(generatedToken.getAccessTokenExpireTime())
//                .build();
//
//        return commonService.successResponse(SuccessCode.EXAMPLE_SUCCESS.getDescription(), HttpStatus.OK, tokenResponseDto);
//    }
//


    public boolean passwordCheck(String email, String password) {
        String entityPassword = memberRepository.findByEmail(email).get().getPassword();
        return bCryptPasswordEncoder.matches(password, entityPassword);
    }



    public CommonResponseDto<Object> findMyInfo(String accessToken) {
        // TODO 예외처리 수정

        Long id = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MYINFO_FAIL.getDescription()));
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .gender(member.getGender())
                .birthday(member.getBirthday())
                .nickname(member.getNickname())
                .phoneNum(member.getPhoneNum())
                .profileImg(member.getProfileImg())
                .isDeleted(member.isDeleted())
                .createdAt(member.getCreatedAt())
                .build();

        // SUCCESS_CODE 수정 필요
        return commonService.successResponse(SuccessCode.MYINFO_SUCCESS.getDescription(), HttpStatus.OK, memberInfoDto);
    }

    public CommonResponseDto<Object> findMyInfoTest(Long id) {
        Member member = memberRepository.findById(id)
//                .orElseThrow(()-> new NotFoundException("")); // 에러코드 입력, 예외처리 필요
                .orElseThrow(() -> new IllegalArgumentException("Unexpected User"));
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(member.getEmail())
                .name(member.getName())
                .address(member.getAddress())
                .gender(member.getGender())
                .birthday(member.getBirthday())
                .nickname(member.getNickname())
                .phoneNum(member.getPhoneNum())
                .profileImg(member.getProfileImg())
                .isDeleted(member.isDeleted())
                .createdAt(member.getCreatedAt())
                .build();

        return commonService.successResponse(SuccessCode.EXAMPLE_SUCCESS.getDescription(), HttpStatus.OK, memberInfoDto);
    }



}
