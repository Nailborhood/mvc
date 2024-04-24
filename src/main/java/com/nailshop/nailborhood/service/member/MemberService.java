package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Favorite;
import com.nailshop.nailborhood.domain.member.Login;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.domain.review.Review;
import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.member.*;
import com.nailshop.nailborhood.dto.member.request.*;
import com.nailshop.nailborhood.dto.member.response.UserInfoResponseDto;
import com.nailshop.nailborhood.repository.member.CustomerRepository;
import com.nailshop.nailborhood.repository.member.LoginRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.security.dto.GeneratedToken;
import com.nailshop.nailborhood.security.dto.TokenResponseDto;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import com.nailshop.nailborhood.service.common.CommonService;
import com.nailshop.nailborhood.service.s3upload.S3UploadService;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.Role;
import com.nailshop.nailborhood.type.SuccessCode;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
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

    private final S3UploadService s3UploadService;
    private final CommonService commonService;
    private final TokenProvider tokenProvider;

    private final EntityManager entityManager;

    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;

    // 이메일 중복 체크
    public CommonResponseDto<Object> checkEmailIsAvailable(String emailInput) {
        boolean exist = findByEmail(emailInput);
        DuplicationCheckDto duplicationCheckDto = new DuplicationCheckDto();
        duplicationCheckDto.setCheck(emailInput);
        duplicationCheckDto.setExist(exist);
        if (exist)
            return commonService.errorResponse(ErrorCode.EMAIL_NOT_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
        else
            return commonService.successResponse(SuccessCode.EMAIL_AVAILABLE.getDescription(), HttpStatus.OK, duplicationCheckDto);
    }

    // 닉네임 중복 체크
    public CommonResponseDto<Object> checkNicknameIsAvailable(String nicknameInput) {
        boolean exist = findByNickname(nicknameInput);
        DuplicationCheckDto duplicationCheckDto = new DuplicationCheckDto();
        duplicationCheckDto.setCheck(nicknameInput);
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
                    .name(signUpRequestDto.getName())
                    .phoneNum(signUpRequestDto.getPhoneNum())
                    .gender(signUpRequestDto.getGender())
                    .birthday(signUpRequestDto.getBirthday())
                    .profileImg("/assets/icons/user.svg")
                    .role(Role.ROLE_USER)
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
    public boolean findByEmail(String email) {
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

    // 비밀번호 확인
    private boolean passwordCheck(String email, String password) {
        String entityPassword = memberRepository.findByEmail(email).get().getPassword();
        return bCryptPasswordEncoder.matches(password, entityPassword);
    }

    // 내 정보 가져오기 (마이 페이지로 이동 필요)
    public CommonResponseDto<Object> findMyInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MYINFO_FAIL.getDescription()));
        MemberInfoDto memberInfoDto = MemberInfoDto.builder()
                .email(member.getEmail())
                .name(member.getName())
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
    public CommonResponseDto<Object> updateMyInfo(Long id, ModMemberInfoRequestDto modInfoDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MYINFO_FAIL.getDescription()));

        if (findByNickname(modInfoDto.getNickname()) && !member.getNickname().equals(modInfoDto.getNickname())) {
            // 닉네임 중복
            return commonService.errorResponse(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else if (findByPhoneNum(modInfoDto.getPhoneNum()) && !member.getPhoneNum().equals(modInfoDto.getPhoneNum())) {
            // 전화번호 중복
            return commonService.errorResponse(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription(), HttpStatus.BAD_REQUEST, modInfoDto);
        } else {
            try {
                // 정보 업데이트
                memberRepository.updateMemberByMemberId
                        (id, modInfoDto.getNickname(), modInfoDto.getPhoneNum(), modInfoDto.getGender(), modInfoDto.getBirthday());

                return commonService.successResponse(SuccessCode.MYINFO_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);

            } catch (Exception e) {
                return commonService.errorResponse(ErrorCode.MYINFO_UPDATE_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
            }
        }
    }

    // 비밀번호 수정 (마이페이지로 이동 필요)
    @Transactional
    public CommonResponseDto<Object> updatePassword(Long id, ModPasswordRequestDto modPasswordRequestDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        boolean check = passwordCheck(member.getEmail(), modPasswordRequestDto.getOldPassword());
        boolean match = modPasswordRequestDto.getNewPassword().equals(modPasswordRequestDto.getNewPasswordCheck());
        if (!match || !check)
            return commonService.errorResponse(ErrorCode.PASSWORD_CHECK_FAIL.getDescription(), HttpStatus.BAD_REQUEST, modPasswordRequestDto);
        else {

            String encodedPassword = bCryptPasswordEncoder.encode(modPasswordRequestDto.getNewPassword());
            memberRepository.updateMemberPasswordByMemberId(id, encodedPassword);
            return commonService.successResponse(SuccessCode.PASSWORD_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
        }
    }

    // 회원 탈퇴
    @Transactional
    public CommonResponseDto<Object> deleteMember(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        if (member.isDeleted())
            return commonService.errorResponse(ErrorCode.DROPOUT_ALREADY.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        else {
            memberRepository.updateMemberIsDeletedById(id);
            entityManager.clear();
            String email = member.getEmail();
            boolean checkDropout = memberRepository.findByEmail(email).get().isDeleted();

            if (checkDropout)
                return commonService.successResponse(SuccessCode.DROPOUT_SUCCESS.getDescription(), HttpStatus.OK, null);
            else
                return commonService.errorResponse(ErrorCode.DROPOUT_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }


    public CommonResponseDto<Object> findUser(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));

        UserInfoResponseDto userInfoResponseDto =  UserInfoResponseDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .favoriteCnt(member.getFavoriteList()
                        .stream()
                        .filter(Favorite::getStatus)
                        .count()
                )
                .reviewCnt(member.getCustomer().getReviewList()
                        .stream()
                        .filter(review -> !review.isDeleted())
                        .count()
                )
                .reservationCnt(0L)
                .build();
        return commonService.successResponse(SuccessCode.MEMBER_FOUND.getDescription(), HttpStatus.OK, userInfoResponseDto);
    }



    // 프로필 수정
    public CommonResponseDto<Object> updateProfileImg(Long id, MultipartFile multipartFile) {

        // TODO 기본이미지로 변경할 시에는 어떻게 해야하는가?
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
        try {
            String imgPath = s3UploadService.profileImgUpload(multipartFile);

            memberRepository.updateMemberProfileImg(id, imgPath);

            return commonService.successResponse(SuccessCode.PROFILE_UPDATE_SUCCESS.getDescription(), HttpStatus.OK, null);
        } catch (Exception e) {
            return commonService.errorResponse(ErrorCode.PROFILE_UPDATE_FAIL.getDescription(), HttpStatus.INTERNAL_SERVER_ERROR, null);
        }
    }

    public Member findMemberForOAuth2Login(String email) {
        return memberRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException(ErrorCode.MEMBER_NOT_FOUND.getDescription()));
    }

    public SessionDto getSessionDto(Authentication authentication, MemberDetails memberDetails) {
        Member member;
        if (memberDetails == null) {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            Map<String, Object> attributes = oAuth2User.getAttributes();
            member = findMemberForOAuth2Login((String) attributes.get("email"));
        } else {
            member = memberDetails.getMember();
        }
        return SessionDto.of(member);
    }
}