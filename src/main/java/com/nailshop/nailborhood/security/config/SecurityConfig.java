package com.nailshop.nailborhood.security.config;

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
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Configuration
public class SecurityConfig {

    private final TokenProvider tokenProvider;

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
                                .anyRequest().authenticated()
                )
//                .formLogin((form) ->
//                        form
//                                .loginPage("/login")
//                                .usernameParameter("email")
//                                .passwordParameter("password")
//                                .loginProcessingUrl("/loginProc")
//                                .defaultSuccessUrl("/",true)
//                )
                .logout((logout) ->
                        logout
                                .logoutUrl("/logout")
                                .logoutSuccessUrl("/")
                                .deleteCookies("refreshToken")
                                .invalidateHttpSession(true)
                )
                .csrf((csrf) ->
                        csrf
                                .disable()
                )
//                .cors((cors) -> cors.configure(http))
                .sessionManagement((sessionManagement)->
                                sessionManagement
                                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                ).exceptionHandling((exceptionConfig) ->
//                        exceptionConfig.authenticationEntryPoint()
                ).addFilterBefore(new JwtAuthenticationFilter(tokenProvider), UsernamePasswordAuthenticationFilter.class)

        ;

        return http.build();

    }


}
