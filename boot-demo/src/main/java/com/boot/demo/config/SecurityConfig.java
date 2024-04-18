package com.boot.demo.config;

import com.boot.demo.jwt.JwtFilter;
import com.boot.demo.jwt.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${frontDomain}")
    private String frontDomain;

    private final TokenProvider tokenProvider;
    @Bean
    public PasswordEncoder passwordEncoder() {return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .httpBasic(
                        basic ->basic.disable()
                        //기본 인증 끔
                )
                .csrf( csrf-> csrf.disable()
                )
                .logout(
                        logout -> logout.clearAuthentication(true)
                )
                .sessionManagement(
                        sessionManage -> sessionManage.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(
                        //jwt필터를 이용하도록 설정
                        new JwtFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .authorizeHttpRequests(request -> {
                    request
                            .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                            .requestMatchers(antMatcher("/auth/**")).permitAll()
                            .anyRequest().authenticated();
                        })
                .cors(
                        cors->cors.configurationSource(configurationSource())
                );


        return http.build();
    }

    @Bean
    public CorsConfigurationSource configurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.addAllowedOrigin(frontDomain);
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setMaxAge(86400L);
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",config);
        return source;
    }

}
