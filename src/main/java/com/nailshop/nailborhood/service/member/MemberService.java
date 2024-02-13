package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Login;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.repository.member.LoginRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.security.dto.GeneratedToken;
import com.nailshop.nailborhood.security.dto.TokenResponseDto;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.SuccessCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@RequiredArgsConstructor
@Slf4j
@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final LoginRepository loginRepository;

    private final CommonService commonService;
    private final TokenProvider tokenProvider;

    private final EntityManager entityManager;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public CommonResponseDto<Object> checkEmailIsAvailable(CheckDto checkDto) {
        boolean exist = findByEmail(checkDto.getCheck());
        checkDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else
            return commonService.successResponse(SuccessCode.EMAIL_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

    public CommonResponseDto<Object> checkNicknameIsAvailable(CheckDto checkDto) {
        boolean exist = findByNickname(checkDto.getCheck());
        checkDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else
            return commonService.successResponse(SuccessCode.NICKNAME_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

    public CommonResponseDto<Object> checkPhoneNumIsAvailable(CheckDto checkDto) {
        boolean exist = findByPhoneNum(checkDto.getCheck());
        checkDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
        else
            return commonService.successResponse(SuccessCode.PHONENUM_AVAILABLE.getDescription(), HttpStatus.OK, checkDto);
    }

    public CommonResponseDto<Object> signUp(SignUpDto signUpDto) {
        if (findByEmail(signUpDto.getEmail()))
            return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else if (findByNickname(signUpDto.getNickname()))
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else if (findByPhoneNum(signUpDto.getPhoneNum()))
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
        else if (!matchWithPasswordPattern(signUpDto.getPassword()))
            return commonService.errorResponse(ErrorCode.PASSWORD_NOT_MATCH_WITH_PATTERN.getDescription(), HttpStatus.BAD_REQUEST, signUpDto);
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
                // Customer 테이블 컬럼 생성 필요
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
        if (!findByEmail(loginDto.getEmail()))
            return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED, null);
        else if (findByEmail(loginDto.getEmail()) && passwordCheck(loginDto.getEmail(), loginDto.getPassword())) {
            Member member = memberRepository.findByEmail(loginDto.getEmail()).get();
            if (member.isDeleted())
                return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED, null);
            else {
                // 토큰 발급
                GeneratedToken generatedToken = tokenProvider.generateToken(member);

                Optional<Login> optionalLogin = loginRepository.findByMember_MemberId(member.getMemberId());
                Login login;
                if (optionalLogin.isEmpty()) {
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

                ResponseCookie responseCookie = ResponseCookie
                        .from("refreshToken", generatedToken.getRefreshToken())
                        .maxAge(Duration.ofDays(7))
                        .path("/")
                        .secure(true)
                        .sameSite("None")
                        .httpOnly(false)
                        .domain("localhost")
                        .build();

                Map<String, Object> loginMap = new HashMap<>();
                loginMap.put("accessToken", tokenResponseDto);
                loginMap.put("refreshToken", responseCookie);

                return commonService.successResponse(SuccessCode.LOGIN_SUCCESS.getDescription(), HttpStatus.OK, loginMap);
            }
        } else {
            return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED, null);
        }
    }

    @Transactional
    public CommonResponseDto<Object> renewToken(String refreshToken) {
        // 유저 get
        Login login = loginRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundException(ErrorCode.EXAMPLE_EXCEPTION.getDescription()));

        Member member = login.getMember();

        GeneratedToken generatedToken = tokenProvider.generateToken(member);

        login.updateToken(generatedToken.getRefreshToken());

        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .accessToken(generatedToken.getAccessToken())
                .accessTokenExpireTime(generatedToken.getAccessTokenExpireTime())
                .build();

        return commonService.successResponse(SuccessCode.EXAMPLE_SUCCESS.getDescription(), HttpStatus.OK, tokenResponseDto);
    }



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


    @Transactional
    public CommonResponseDto<Object> updateMyInfo(String accessToken, ModMemberInfoRequestDto modInfoDto) {
        Long id = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MYINFO_FAIL.getDescription()));

        if (findByNickname(modInfoDto.getNickname()) && !member.getNickname().equals(modInfoDto.getNickname())) {
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else if (findByPhoneNum(modInfoDto.getPhoneNum()) && !member.getPhoneNum().equals(modInfoDto.getPhoneNum())) {
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else {
            memberRepository.updateMemberByMemberId
                    (id, modInfoDto.getAddress(), modInfoDto.getNickname(), modInfoDto.getPhoneNum(), modInfoDto.getGender(), modInfoDto.getBirthday());

            boolean nicknameUpdated = memberRepository.findByNickname(modInfoDto.getNickname()).isPresent();
            boolean phoneNumUpdated = memberRepository.findByPhoneNum(modInfoDto.getPhoneNum()).isPresent();

            if (nicknameUpdated && phoneNumUpdated)
                return commonService.successResponse(SuccessCode.MYINFO_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
            else
                return commonService.errorResponse(ErrorCode.MYINFO_UPDATE_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    @Transactional
    public CommonResponseDto<Object> updateMyInfoTest(Long id, ModMemberInfoRequestDto modInfoDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MYINFO_FAIL.getDescription()));

        if (findByNickname(modInfoDto.getNickname()) && !member.getNickname().equals(modInfoDto.getNickname())) {
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else if (findByPhoneNum(modInfoDto.getPhoneNum()) && !member.getPhoneNum().equals(modInfoDto.getPhoneNum())) {
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else {
            memberRepository.updateMemberByMemberId
                    (id, modInfoDto.getAddress(), modInfoDto.getNickname(), modInfoDto.getPhoneNum(), modInfoDto.getGender(), modInfoDto.getBirthday());

            boolean nicknameUpdated = memberRepository.findByNickname(modInfoDto.getNickname()).isPresent();
            boolean phoneNumUpdated = memberRepository.findByPhoneNum(modInfoDto.getNickname()).isPresent();

            if (nicknameUpdated && phoneNumUpdated)
                return commonService.successResponse(SuccessCode.MYINFO_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
            else
                return commonService.errorResponse(ErrorCode.MYINFO_UPDATE_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);

        }
    }

    @Transactional
    public CommonResponseDto<Object> logout(String accessToken) {
        Long id = tokenProvider.getUserId(accessToken);
        Login login = loginRepository.findByMember_MemberId(id)
                .orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        loginRepository.updateRefreshTokenByMemberId(id, null);

            return commonService.successResponse(SuccessCode.LOGIN_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    public CommonResponseDto<Object> beforeUpdatePassword(String accessToken, BeforeModPasswordCheckRequestDto beforeModPasswordCheckRequestDto){
        Long id = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        boolean match = passwordCheck(member.getEmail(), beforeModPasswordCheckRequestDto.getPasswordCheck());
        return commonService.successResponse(SuccessCode.PASSWORD_CHECK_SUCCESS.getDescription(), HttpStatus.OK, match);
    }

    @Transactional
    public CommonResponseDto<Object> updatePassword(String accessToken, ModPasswordRequestDto modPasswordRequestDto) {
        boolean match = modPasswordRequestDto.getPassword().equals(modPasswordRequestDto.getPasswordCheck());
        if(!match)
            return commonService.errorResponse(ErrorCode.PASSWORD_CHECK_FAIL.getDescription(), HttpStatus.BAD_REQUEST, modPasswordRequestDto);
        else{
            Long id = tokenProvider.getUserId(accessToken);
            Member member = memberRepository.findById(id)
                    .orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));

            String encodedPassword = bCryptPasswordEncoder.encode(modPasswordRequestDto.getPassword());
            memberRepository.updateMemberPasswordByMemberId(id,encodedPassword);

            return commonService.successResponse(SuccessCode.PASSWORD_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
        }
    }

    @Transactional
    public CommonResponseDto<Object> deleteMember(String accessToken) {
        Long id = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        if(member.isDeleted()) return commonService.errorResponse(ErrorCode.DROPOUT_ALREADY.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        else {
            memberRepository.updateMemberIsDeletedById(id);
            entityManager.flush();
            String email = member.getEmail();
            boolean checkDropout = memberRepository.findByEmail(email).get().isDeleted();

            // 검증로직 점검 필요, 영속성 컨텍스트 해결 필요
            if(checkDropout)
                return commonService.successResponse(SuccessCode.DROPOUT_SUCCESS.getDescription(), HttpStatus.OK, null);
            else
                return commonService.errorResponse(ErrorCode.DROPOUT_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }
}