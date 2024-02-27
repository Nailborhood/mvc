package com.nailshop.nailborhood.security.config;

import com.nailshop.nailborhood.security.config.exceptionHandler.CustomAccessDeniedHandler;
import com.nailshop.nailborhood.security.config.exceptionHandler.CustomAuthenticationEntryPoint;
import com.nailshop.nailborhood.security.config.jwt.JwtAuthenticationFilter;
import com.nailshop.nailborhood.security.service.jwt.TokenProvider;
import jakarta.servlet.DispatcherType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(PathRequest.toStaticResources()
                        .atCommonLocations()
                );
    }

    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authorize) ->
                        authorize
                                .dispatcherTypeMatchers(DispatcherType.FORWARD).permitAll()
                                .requestMatchers("/**").permitAll()
                                .requestMatchers("/v3/api-docs*/**", "/configuration/**", "/swagger*/**", "/webjars/**",
                                        "/**/favicon.ico", "/favicon.ico", "/error**" , "/api/**" , "/env" , "/**/env").permitAll()
//                                .anyRequest().authenticated()
                )
                .formLogin((form) ->
                        form
                                .loginPage("/loginProc")
                                .usernameParameter("email")
                                .passwordParameter("password")
                                .defaultSuccessUrl("/nailborhood/mypage/myInfo",true)
                )
                .logout((logout) ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .deleteCookies("refreshToken")
                                .invalidateHttpSession(true)
                )
//                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf((csrf) ->
                        csrf
                                .disable()
                )
                .cors((cors) -> cors
                        .configurationSource(configurationSource())
                )
                .sessionManagement((sessionManagement)->
                                sessionManagement
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                ).exceptionHandling((exceptionConfig) ->
//                        exceptionConfig.authenticationEntryPoint()
                )
                .exceptionHandling(httpSecurityExceptionHandlingConfigurer ->
                        httpSecurityExceptionHandlingConfigurer
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler)
                )
                .addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)
        ;

        return http.build();

    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOriginPattern("https://nailborhood.shop");
//        configuration.addAllowedOriginPattern("https://nailborhood.shop:8080");
//        configuration.addAllowedOriginPattern("https://nailborhood.shop:8081");
        configuration.addAllowedOriginPattern("http://localhost:3000");
        configuration.addAllowedOriginPattern("http://localhost:8080");
        configuration.addAllowedOriginPattern("https://localhost:3000");
        configuration.addAllowedMethod("GET");
        configuration.addAllowedMethod("POST");
        configuration.addAllowedMethod("PUT");
        configuration.addAllowedMethod("DELETE");
        configuration.addAllowedMethod("PATCH");
        configuration.addAllowedMethod("OPTIONS");
        configuration.addAllowedHeader("*");
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
