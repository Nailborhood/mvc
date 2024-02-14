package com.nailshop.nailborhood.service.member;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.member.request.ModMemberInfoRequestDto;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import com.nailshop.nailborhood.type.ErrorCode;
import com.nailshop.nailborhood.type.SuccessCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired
    MemberService memberService;

    @Autowired
    MemberRepository memberRepository;

//    @BeforeEach
    @Test
    public void before() {
        LocalDate date = LocalDate.of(2022, 2, 8);
        Member member1 = Member.builder()
                .email("test1@email.com")
                .password("12341234")
                .name("test1")
                .nickname("testNickname1")
                .phoneNum("010-0000-0001")
                .gender("F")
                .address("서울시 마포구")
                .birthday(LocalDate.of(1996, 11, 13))
                .build();

        Member member2 = Member.builder()
                .email("test2@email.com")
                .password("12341234")
                .name("test2")
                .nickname("testNickname2")
                .phoneNum("010-0000-0002")
                .gender("M")
                .address("서울시 서대문구")
                .birthday(LocalDate.of(1995, 8, 25))
                .build();

        memberRepository.save(member1);
        memberRepository.save(member2);
    }





    @DisplayName("updateMember_updated() : 내 정보 수정 완료")
    @Test
    public void updateMember_updated() {
        ModMemberInfoRequestDto modMemberInfoRequestDto = ModMemberInfoRequestDto.builder()
                                                                                 .address("서울시 마포구 공덕동")
                                                                                 .phoneNum("010-0000-0001")
                                                                                 .nickname("testNickname3")
                                                                                 .gender("M")
                                                                                 .birthday(LocalDate.of(1996, 11, 13))
                                                                                 .build();

        Long id = memberRepository.findByEmail("test1@email.com").get().getMemberId();


        String message = memberService.updateMyInfoTest(id, modMemberInfoRequestDto).getMessage();

        assertThat(message).isEqualTo(SuccessCode.MYINFO_UPDATE_SUCCESS.getDescription());
    }

    @DisplayName("updateMember_nickname_overlapped() : 내 정보 수정 실패, 닉네임 중복")
    @Test
    public void updateMember_nickname_overlapped() {
        ModMemberInfoRequestDto modMemberInfoRequestDto = ModMemberInfoRequestDto.builder()
                .address("서울시 마포구 공덕동")
                .phoneNum("010-0000-0001")
                .nickname("testNickname2")
                .gender("M")
                .birthday(LocalDate.of(1996, 11, 13))
                .build();

        Long id = memberRepository.findByEmail("test1@email.com").get().getMemberId();


        String message = memberService.updateMyInfoTest(id, modMemberInfoRequestDto).getMessage();

        assertThat(message).isEqualTo(ErrorCode.NICKNAME_NOT_AVAILABLE.getDescription());
    }

    @DisplayName("updateMember_phoneNum_overlapped() : 내 정보 수정 실패, 전화번호 중복")
    @Test
    public void updateMember_phoneNum_overlapped() {
        ModMemberInfoRequestDto modMemberInfoRequestDto = ModMemberInfoRequestDto.builder()
                .address("서울시 마포구 공덕동")
                .phoneNum("010-0000-0002")
                .nickname("testNickname1")
                .gender("M")
                .birthday(LocalDate.of(1996, 11, 13))
                .build();

        Long id = memberRepository.findByEmail("test1@email.com").get().getMemberId();


        String message = memberService.updateMyInfoTest(id, modMemberInfoRequestDto).getMessage();

        assertThat(message).isEqualTo(ErrorCode.PHONENUM_NOT_AVAILABLE.getDescription());
    }






}
