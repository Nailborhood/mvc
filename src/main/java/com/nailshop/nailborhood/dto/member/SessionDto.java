package com.nailshop.nailborhood.dto.member;

import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.type.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SessionDto {

    private Long id;
    private String nickname;
    private String profileImg;
    private String email;
    private Role role;
    private String provider;

    @Builder
    public SessionDto(Long id, String nickname, String profileImg, String email, Role role, String provider) {
        this.id = id;
        this.nickname = nickname;
        this.profileImg = profileImg;
        this.email = email;
        this.role = role;
        this.provider = provider;
    }

    public static SessionDto of(Member member) {
        return setSession(member);
    }
//
//    private static SessionDto sessionOfForm(MemberDetails memberDetails) {
//        return SessionDto.builder()
//                .id(memberDetails.getMember().getMemberId())
//                .nickname(memberDetails.getNickname())
//                .profileImg(memberDetails.getMember().getProfileImg())
//                .provider("")
//                .build();
//    }
//
//    private static SessionDto sessionOfGoogle(Member member) {
//        return SessionDto.builder()
//                .id(member.getMemberId())
//                .nickname(member.getNickname())
//                .profileImg(member.getProfileImg())
//                .provider(member.getProvider())
//                .build();
//    }

    private static SessionDto setSession(Member member){
        return SessionDto.builder()
                .id(member.getMemberId())
                .email(member.getEmail())
                .role(member.getRole())
                .nickname(member.getNickname())
                .profileImg(member.getProfileImg())
                .provider(member.getProvider())
                .build();
    }

}
