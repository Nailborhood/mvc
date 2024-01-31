package com.nailshop.nailborhood.controller.example;

import com.nailshop.nailborhood.dto.common.CommonResponseDto;
import com.nailshop.nailborhood.dto.common.ResultDto;
import com.nailshop.nailborhood.dto.example.ExampleDto;
import com.nailshop.nailborhood.service.example.ExampleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ExampleController {

    private final ExampleService exampleService;

    // 반환값이 없을 때
    @PostMapping("/example")
    public ResponseEntity<ResultDto<Void>> examplePost(){
        CommonResponseDto<Object> commonResponseDto = exampleService.examplePost();
        ResultDto<Void> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }

    // 반환값이 있을 때
    @GetMapping("/example")
    public ResponseEntity<ResultDto<ExampleDto>> exampleGet(){
        CommonResponseDto<Object> commonResponseDto = exampleService.exampleGet();
        ResultDto<ExampleDto> resultDto = ResultDto.in(commonResponseDto.getStatus(), commonResponseDto.getMessage());
        resultDto.setData((ExampleDto) commonResponseDto.getData());

        return ResponseEntity.status(commonResponseDto.getHttpStatus()).body(resultDto);
    }
}
