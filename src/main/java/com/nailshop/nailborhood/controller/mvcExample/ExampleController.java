package com.nailshop.nailborhood.controller.mvcExample;

import com.nailshop.nailborhood.security.config.auth.MemberDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
public class ExampleController {

    @GetMapping("/example")
    public String hello(@AuthenticationPrincipal MemberDetails memberDetails, Model model) {
        String nicknameSpace;
        if(memberDetails == null) {
            nicknameSpace = "";
        } else {
            nicknameSpace = memberDetails.getNickname() + "님";
        }
        model.addAttribute("memberNickname", nicknameSpace);
        model.addAttribute("message", "안녕");
        return "example/hello"; //html 이름
    }
}
