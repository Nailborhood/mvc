package com.nailshop.nailborhood;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HealthCheckController {

    @Value("${server.env}")
    private String env;


    @Value("${serverName}")
    private String serverName;




    @GetMapping("/hc")
    public ResponseEntity<?> healthCheck(){
        Map<String,String> responseData = new HashMap<>();
        responseData.put("serverName", serverName);
        responseData.put("env",env);

        return ResponseEntity.ok(responseData);
    }


    @GetMapping("/env")
    public ResponseEntity<?> getEnv(){
        return ResponseEntity.ok(env);
    }
}
