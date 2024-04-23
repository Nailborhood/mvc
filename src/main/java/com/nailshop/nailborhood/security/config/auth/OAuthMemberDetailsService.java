package com.nailshop.nailborhood.security.config.auth;

import com.nailshop.nailborhood.domain.member.Customer;
import com.nailshop.nailborhood.domain.member.Member;
import com.nailshop.nailborhood.dto.member.request.OAuthAttributes;
import com.nailshop.nailborhood.repository.member.CustomerRepository;
import com.nailshop.nailborhood.repository.member.MemberRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class OAuthMemberDetailsService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final MemberRepository memberRepository;
    private final CustomerRepository customerRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId(); // 구글
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        Member member = saveOrUpdateOAuth2Member(attributes);
        saveOAuth2Customer(member);

        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(member.getRole().toString())),
                attributes.getAttributes(),
                userNameAttributeName);
    }

    private Member saveOrUpdateOAuth2Member(OAuthAttributes authAttributes) {
        Member member = memberRepository.findByEmail(authAttributes.getEmail())
                .map(entity -> entity.update(authAttributes.getName(), authAttributes.getPicture()))
                .orElse(authAttributes.toEntity());
        return memberRepository.save(member);
    }

    private void saveOAuth2Customer(Member member) {
        if(customerRepository.findCustomerByMember_MemberId(member.getMemberId()).isEmpty()) {
            Customer customer = Customer.builder()
                    .member(member)
                    .build();
            customerRepository.save(customer);
        }
    }
}
