package com.nailshop.nailborhood.security.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static com.nailshop.nailborhood.security.service.jwt.TokenProvider.AUTH;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        Info info = new Info()
                .title("Nailborhood API")
                .version("v.1.0.0")
                .description("내동네일의 api 명세서입니다.");


        return new OpenAPI()
                .info(info)
                .addSecurityItem(requirement())
                .components(components())
                ;
    }

     private SecurityRequirement requirement () {
       return new SecurityRequirement()
                .addList(AUTH);
     }

     private Components components() {
        return new Components()
                .addSecuritySchemes(AUTH, new SecurityScheme()
                        .name(AUTH)
                        .type(SecurityScheme.Type.APIKEY)
                        .in(SecurityScheme.In.HEADER)
                        .bearerFormat(AUTH)
                );

     }

}
