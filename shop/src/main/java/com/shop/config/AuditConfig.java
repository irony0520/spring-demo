package com.shop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

//spring 구성 클래스
@Configuration
@EnableJpaAuditing
//EnableJpaAuditing --> jpa auditing 기능 활성화 어노테이션 auditorAware빈을 설정할시 스프링이 자동으로  AuditorAwareImpl를 인식, 사용할 수 있게됨
public class AuditConfig {

    //auditorAware클래스를 빈으로 등록
    @Bean
    public AuditorAware<String> auditorProvider(){


    return new AuditorAwareImpl();
    }
}
