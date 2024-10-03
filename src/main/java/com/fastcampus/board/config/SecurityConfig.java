package com.fastcampus.board.config;

import com.fastcampus.board.dto.UserAccountDto;
import com.fastcampus.board.dto.security.BoardPrincipal;
import com.fastcampus.board.repository.UserAccountRepository;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
                // Static 리소스 요청은 모두 허용
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                // 조회(GET) 요청에 대한 특정 경로는 모두 허용
                .requestMatchers(HttpMethod.GET).permitAll()
                // 그 외의 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.permitAll()) // 폼로그인 설정 & 로그인 페이지 접근 허용
            .logout(logout -> logout.logoutSuccessUrl("/").permitAll()) // 로그아웃 설정 및 로그아웃 성공시 리다이렉션 URL 넣고 접근 허용함
            .csrf(x -> x.disable());    // Disable CSRF(Cross site Request forgery) protection

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService(UserAccountRepository userAccountRepository) {
        return username -> userAccountRepository
            .findById(username)
            .map(UserAccountDto::from)
            .map(BoardPrincipal::from)
            .orElseThrow(() -> new UsernameNotFoundException("유저를 찾을 수 없습니다 - username: " + username));
    }
}