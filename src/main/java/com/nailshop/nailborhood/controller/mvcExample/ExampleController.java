package com.nailshop.nailborhood.controller.mvcExample;

import com.nailshop.nailborhood.dto.member.SessionDto;
import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import com.nailshop.nailborhood.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ExampleController {

    private final MemberService memberService;

    @GetMapping("/example")
    public String hello(Authentication authentication,
                        @AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        if(authentication != null) {
            SessionDto sessionDto = memberService.getSessionDto(authentication,memberDetails);
            model.addAttribute("sessionDto", sessionDto);
        }
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        System.out.println(SecurityContextHolder.getContext().getAuthentication().getAuthorities());

        model.addAttribute("message", "안녕");
        return "example/hello"; //html 이름
    }
    
    @GetMapping("/example/mypage")
    public String exampleMypage(Model model) {
        return "example/mypage_example"; //html 이름
    }

    @GetMapping("/example/owner")
    public String exampleOwner(Model model) {
        return "example/owner_example"; //html 이름
    }

    @GetMapping("/example/admin")
    public String exampleAdmin(Model model) {
        return "example/admin_example"; //html 이름
    }
}
