package com.sascom.chickenstock.global.config;

import com.sascom.chickenstock.global.filter.LoggingFilter;
import com.sascom.chickenstock.global.jwt.JwtAuthenticationEntryPoint;
import jakarta.servlet.Filter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandlerImpl;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableMethodSecurity
@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

    private final Filter jwtAuthenticationFilter;
    private final DefaultOAuth2UserService chickenstockOauth2MemberService;
    private final AuthenticationSuccessHandler oauth2SuccessHandler;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    public WebSecurityConfig(
            @Qualifier("jwtAuthenticationFilter")
            Filter jwtAuthenticationFilter,
            DefaultOAuth2UserService chickenstockOauth2MemberService,
            AuthenticationSuccessHandler oauth2SuccessHandler,
            JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint
    ) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.chickenstockOauth2MemberService = chickenstockOauth2MemberService;
        this.oauth2SuccessHandler = oauth2SuccessHandler;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http
                .headers(headers -> headers.frameOptions(FrameOptionsConfig::disable).disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        http
                .authorizeHttpRequests(
                        request -> request.requestMatchers(
                                        new AntPathRequestMatcher("/error"),
                                        new AntPathRequestMatcher("/favicon.ico"),
                                        new AntPathRequestMatcher("/actuator/prometheus"),
                                        new AntPathRequestMatcher("/"),
                                        new AntPathRequestMatcher("/auth/**")
                                ).permitAll()
                                .anyRequest().authenticated()
                )

                .oauth2Login(
                        oauth -> oauth.userInfoEndpoint(
                                        endpoint -> endpoint.userService(chickenstockOauth2MemberService)
                                )
                                .successHandler(oauth2SuccessHandler)
                );

        http
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new LoggingFilter(), jwtAuthenticationFilter.getClass());

        http
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                        .accessDeniedHandler(new AccessDeniedHandlerImpl())
                );

        return http.build();
    }
}
