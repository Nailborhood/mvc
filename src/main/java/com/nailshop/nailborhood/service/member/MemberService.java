package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Login;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.dto.member.request.*;
import com.nailshop.nailborhood.repository.member.CustomerRepository;
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
    private final CustomerRepository customerRepository;

    private final CommonService commonService;
    private final TokenProvider tokenProvider;

    private final EntityManager entityManager;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    // 이메일 중복 체크
    public CommonResponseDto<Object> checkEmailIsAvailable(DuplicationCheckDto duplicationCheckDto) {
        boolean exist = findByEmail(duplicationCheckDto.getCheck());
        duplicationCheckDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
        else
            return commonService.successResponse(SuccessCode.EMAIL_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
    }

    // 닉네임 중복 체크
    public CommonResponseDto<Object> checkNicknameIsAvailable(DuplicationCheckDto duplicationCheckDto) {
        boolean exist = findByNickname(duplicationCheckDto.getCheck());
        duplicationCheckDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
        else
            return commonService.successResponse(SuccessCode.NICKNAME_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
    }

    // 전화번호 중복 체크
    public CommonResponseDto<Object> checkPhoneNumIsAvailable(DuplicationCheckDto duplicationCheckDto) {
        boolean exist = findByPhoneNum(duplicationCheckDto.getCheck());
        duplicationCheckDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
        else
            return commonService.successResponse(SuccessCode.PHONENUM_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
    }

    // 회원 가입
    public CommonResponseDto<Object> signUp(SignUpRequestDto signUpRequestDto) {
        // 이메일, 닉네임, 전화번호 중복 확인 + 비밀번호 규칙 확인
        if (findByEmail(signUpRequestDto.getEmail()))
            return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpRequestDto);
        else if (findByNickname(signUpRequestDto.getNickname()))
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpRequestDto);
        else if (findByPhoneNum(signUpRequestDto.getPhoneNum()))
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, signUpRequestDto);
        else if (!matchWithPasswordPattern(signUpRequestDto.getPassword()))
            return commonService.errorResponse(ErrorCode.PASSWORD_NOT_MATCH_WITH_PATTERN.getDescription(), HttpStatus.BAD_REQUEST, signUpRequestDto);
        else {
            // 비밀번호 암호화
            String encPassword = bCryptPasswordEncoder.encode(signUpRequestDto.getPassword());
            // member, customer 생성
            Member member = Member.builder()
                    .email(signUpRequestDto.getEmail())
                    .password(encPassword)
                    .nickname(signUpRequestDto.getNickname())
                    .address(signUpRequestDto.getAddress())
                    .name(signUpRequestDto.getName())
                    .phoneNum(signUpRequestDto.getPhoneNum())
                    .gender(signUpRequestDto.getGender())
                    .birthday(signUpRequestDto.getBirthday())
                    .profileImg("defaultImage")
                    .role(Role.USER)
                    .provider("Nail")
                    .isDeleted(false)
                    .build();
            Customer customer = Customer.builder()
                    .member(member)
                    .build();
            try {
                memberRepository.save(member);
                customerRepository.save(customer);
                return commonService.successResponse(SuccessCode.SIGNUP_SUCCESS.getDescription(), HttpStatus.OK, null);
            } catch (Exception e) {
                return commonService.errorResponse(ErrorCode.SIGNUP_FAIL.getDescription(), HttpStatus.BAD_GATEWAY, signUpRequestDto);
            }
        }
    }

    // Member 찾기 - 이메일, 닉네임, 전화번호
    private boolean findByEmail(String email) {
        Optional<Member> memberOptional = memberRepository.findByEmail(email);
        return memberOptional.isPresent();
    }
    private boolean findByNickname(String nickname) {
        Optional<Member> memberOptional = memberRepository.findByNickname(nickname);
        return memberOptional.isPresent();
    }
    private boolean findByPhoneNum(String phoneNum) {
        Optional<Member> memberOptional = memberRepository.findByPhoneNum(phoneNum);
        return memberOptional.isPresent();
    }

    // 비밀번호 규칙 확인
    private boolean matchWithPasswordPattern(String password) {
        String passwordPattern = "^[A-Za-z0-9]{8,20}$";
        return Pattern.matches(passwordPattern, password);
    }

    // 로그인
    @Transactional
    public CommonResponseDto<Object> memberLogin(LoginRequestDto loginRequestDto) {
        //TODO - 비밀번호가 틀린경우 에러메세지가 안넘어옴 확인 필요

        if (!findByEmail(loginRequestDto.getEmail())) // 이메일이 없는 경우
            return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED, null);
        else if (findByEmail(loginRequestDto.getEmail()) && passwordCheck(loginRequestDto.getEmail(), loginRequestDto.getPassword())) {
            // 이메일이 존재, 비밀번호가 맞는 경우
            Member member = memberRepository.findByEmail(loginRequestDto.getEmail()).get();
            if (member.isDeleted()) // 이미 탈퇴한 회원인 경우
                return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED, null);
            else {
                // 토큰 발급
                GeneratedToken generatedToken = tokenProvider.generateToken(member);

                // refresh token db에 저장
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

                Long id = member.getMemberId();
                // 헤더에 넣을 access token
                TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                        .memberId(id)
                        .accessToken(generatedToken.getAccessToken())
                        .accessTokenExpireTime(generatedToken.getAccessTokenExpireTime())
                        .build();

                // 쿠키에 넣을 refresh token
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
        } else { // 비밀번호가 틀린 경우
            return commonService.errorResponse(ErrorCode.LOGIN_FAIL.getDescription(), HttpStatus.UNAUTHORIZED, null);
        }
    }

    @Transactional
    public CommonResponseDto<Object> renewToken(String refreshToken) {

        // 유저 get
        Login login = loginRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));

        Member member = login.getMember();

        GeneratedToken generatedToken = tokenProvider.generateToken(member);

        login.updateToken(generatedToken.getRefreshToken());

        Long id = member.getMemberId();

        // 헤더에 넣을 access token
        TokenResponseDto tokenResponseDto = TokenResponseDto.builder()
                .memberId(id)
                .accessToken(generatedToken.getAccessToken())
                .accessTokenExpireTime(generatedToken.getAccessTokenExpireTime())
                .build();

        // 쿠키에 넣을 refresh token
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

        return commonService.successResponse(SuccessCode.EXAMPLE_SUCCESS.getDescription(), HttpStatus.OK, loginMap);
    }

    // 비밀번호 확인
    private boolean passwordCheck(String email, String password) {
        String entityPassword = memberRepository.findByEmail(email).get().getPassword();
        return bCryptPasswordEncoder.matches(password, entityPassword);
    }

    // 내 정보 가져오기 (마이 페이지로 이동 필요)
    public CommonResponseDto<Object> findMyInfo(String accessToken) {

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

        return commonService.successResponse(SuccessCode.MYINFO_SUCCESS.getDescription(), HttpStatus.OK, memberInfoDto);
    }

    // 내 정보 업데이트 (마이페이지로 이동 필요)
    @Transactional
    public CommonResponseDto<Object> updateMyInfo(String accessToken, ModMemberInfoRequestDto modInfoDto) {
        Long id = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MYINFO_FAIL.getDescription()));

        if (findByNickname(modInfoDto.getNickname()) && !member.getNickname().equals(modInfoDto.getNickname())) {
            // 닉네임 중복
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else if (findByPhoneNum(modInfoDto.getPhoneNum()) && !member.getPhoneNum().equals(modInfoDto.getPhoneNum())) {
            // 전화번호 중복
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else {
            try{
                // 정보 업데이트
                memberRepository.updateMemberByMemberId
                        (id, modInfoDto.getAddress(), modInfoDto.getNickname(), modInfoDto.getPhoneNum(), modInfoDto.getGender(), modInfoDto.getBirthday());

                    return commonService.successResponse(SuccessCode.MYINFO_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);

            } catch (Exception e) {
                return commonService.errorResponse(ErrorCode.MYINFO_UPDATE_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
            }
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

    // 로그아웃
    @Transactional
    public CommonResponseDto<Object> logout(String accessToken) {
        Long id = tokenProvider.getUserId(accessToken);
        Login login = loginRepository.findByMember_MemberId(id)
                .orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        loginRepository.updateRefreshTokenByMemberId(id, null);

            return commonService.successResponse(SuccessCode.LOGIN_SUCCESS.getDescription(), HttpStatus.OK, null);
    }

    // 비밀번호 수정 전 비밀번호 확인 (마이페이지로 이동 핋요)
    public CommonResponseDto<Object> beforeUpdatePassword(String accessToken, BeforeModPasswordCheckRequestDto beforeModPasswordCheckRequestDto){
        Long id = tokenProvider.getUserId(accessToken);
        Member member = memberRepository.findById(id)
                .orElseThrow(()->new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        boolean match = passwordCheck(member.getEmail(), beforeModPasswordCheckRequestDto.getPasswordCheck());
        return commonService.successResponse(SuccessCode.PASSWORD_CHECK_SUCCESS.getDescription(), HttpStatus.OK, match);
    }

    // 비밀번호 수정 (마이페이지로 이동 필요)
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

    // 회원 탈퇴
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