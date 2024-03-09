package com.nailshop.nailborhood.controller.mvcExample;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/nailborhood") // 로컬테스트용
public class ExampleController {

    @GetMapping("/example")
    public String exampleHello(Model model) {
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
