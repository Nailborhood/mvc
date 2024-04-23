package com.nailshop.nailborhood.dto.member.request;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.type.Role;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Getter
@NoArgsConstructor
//@NoArgsConstructor
public class OAuthAttributes {

    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }

    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes) {
//        if ("naver".equals(registrationId)) {
//            return ofNaver("id", attributes);
//        }
        return ofGoogle(userNameAttributeName, attributes);
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey("google")
                .build();
    }

//    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
//        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
//
//        return OAuthAttributes.builder()
//                .name((String) response.get("name"))
//                .email((String) response.get("email"))
//                .picture((String) response.get("profile_image"))
//                .attributes(response)
//                .nameAttributeKey(userNameAttributeName)
//                .build();
//    }

    public Member toEntity() {
        String UUIDPassword = UUID.randomUUID().toString().substring(0,8);
        Member member = Member.builder()
                .name(name)
                .email(email)
                .nickname(name)
                .profileImg(picture)
                .password(UUIDPassword)
                .role(Role.ROLE_USER)
                .provider(nameAttributeKey)
                .isDeleted(false)
                .build();
        Customer customer = Customer.builder()
                .member(member)
                .build();
        return member;
    }
}
