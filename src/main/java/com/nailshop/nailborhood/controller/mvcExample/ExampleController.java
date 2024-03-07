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
    public String hello(Model model) {
        model.addAttribute("message", "안녕");
        return "example/hello"; //html 이름
    }
}
