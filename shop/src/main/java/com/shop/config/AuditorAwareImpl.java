package com.shop.config;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

//엔티티의 생성과 수저을 감시하는 class상속, optional<t> t는 사용자를 나타냄, 여기서는 string이 들어감 (userid)userId = authentication.getName();
public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String userId= "";
        //authentication이 null이 아닌경우 = 인증정보가 존재해 인증이 완료된 경우에만 userId를 가져오겠다
        if(authentication != null) {
            userId = authentication.getName();
        }
        return Optional.of(userId);
    }
}
