package com.study.sns.configuration;

import com.study.sns.configuration.filter.JwtTokenFilter;
import com.study.sns.exception.CustomAuthenticationEntryPoint;
import com.study.sns.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity //spring Security설정
@RequiredArgsConstructor
public class AuthenticationConfig extends WebSecurityConfigurerAdapter {

    private final UserService userService;

    @Value("${jwt.secret-key}")
    private String key;

    // 1차 필터링
    @Override
    public void configure(WebSecurity web) throws Exception {
        // /api로 시작하는 path만 통과시키고 그게 아니면 ignore한다.
        web.ignoring().regexMatchers("^(?!/api/).*")
                .antMatchers(HttpMethod.POST, "/api/*/users/join", "/api/*/users/login");
    }

    // 2차 필터링
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/api/*/users/join", "/api/*/users/login").permitAll() // v1,v2등 버전정보는 상관없이 허용되도록 해줬다.
                .antMatchers("/api/**").authenticated()
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // session은 따로 관리를 안한다.
                .and()
                .addFilterBefore(new JwtTokenFilter(key, userService), UsernamePasswordAuthenticationFilter.class) //UsernamePasswordAuthenticationFilter 이것 이전에 JwtTokenFilter이거를 적용시켜라 라는의미다.
                .exceptionHandling()
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }
}
