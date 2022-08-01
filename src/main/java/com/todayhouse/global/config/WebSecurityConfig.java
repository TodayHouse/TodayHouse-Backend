package com.todayhouse.global.config;

import com.todayhouse.domain.user.oauth.application.CustomOAuth2UserService;
import com.todayhouse.domain.user.oauth.dao.HttpCookieOAuth2AuthorizationRequestRepository;
import com.todayhouse.global.config.jwt.JwtAuthenticationFilter;
import com.todayhouse.global.config.jwt.JwtTokenProvider;
import com.todayhouse.global.config.oauth.OAuth2AuthenticationFailureHandler;
import com.todayhouse.global.config.oauth.OAuth2AuthenticationSuccessHandler;
import com.todayhouse.global.config.oauth.RestAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@EnableConfigurationProperties
@RequiredArgsConstructor
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final RestAuthenticationEntryPoint restAuthenticationEntryPoint;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final HttpCookieOAuth2AuthorizationRequestRepository authorizationRequestRepository;

    // 정적 자원에 대해서는 Security 설정을 적용하지 않음.
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .and()
                .ignoring().antMatchers("/swagger-ui/**",
                        "/v2/api-docs", "/v3/api-docs",
                        "/swagger-resources",
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources/configuration/security");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .httpBasic().disable()
                .csrf().disable() // csrf 보안 토큰 disable처리
                .headers().frameOptions().disable()
                .and()
                .cors()
                .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 토큰 기반 인증이므로 세션 역시 사용하지 않습니다.
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests() // 요청에 대한 사용권한 체크
                .antMatchers(HttpMethod.POST, "/categories")
                .hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/categories/**")
                .hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.PATCH, "/categories")
                .hasAnyRole("ADMIN")
                .antMatchers(HttpMethod.POST, "/follows", "/sellers", "/products/**", "/stories/**",
                        "/options/**", "/orders/**", "/reviews/**", "/users/info", "/inquires/**", "/scraps/**", "/likes/**")
                .hasAnyRole("USER", "ADMIN") // user, admin post 요청만 허용
                .antMatchers(HttpMethod.DELETE, "/follows", "/products/**", "/stories/**",
                        "/options/**", "/reviews/**", "/inquires/**", "/scraps/**", "/likes/**")
                .hasAnyRole("USER", "ADMIN") // user, admin delete 요청만 허용
                .antMatchers(HttpMethod.PUT, "/products/**", "/options/**", "/orders/**")
                .hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/stories/**")
                .hasAnyRole("USER", "ADMIN")
                .antMatchers("/users/password/new", "/users/signup", "/oauth2/**")
                .hasAnyRole("GUEST") // guest 요청만 허용
                .antMatchers()
                .authenticated()// 인증된 요청만 허용

                .antMatchers(HttpMethod.GET, "/categories/**", "/options/**", "/products/**", "/stories/**",
                        "/follows/**", "/sellers/**", "/users/**", "/orders/**", "/reviews/**",
                        "/inquires/**", "/scraps/**")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/users/login")
                .anonymous()
                .antMatchers("/emails/**")
                .permitAll()// 모든 요청 허용

                .anyRequest().denyAll()//그외 모든 요청 거부

                .and()
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class)
                .oauth2Login()
                .authorizationEndpoint().baseUri("/oauth2/authorize")
                .authorizationRequestRepository(authorizationRequestRepository)
                .and()
                .userInfoEndpoint()
                .userService(customOAuth2UserService)
                .and()
                .successHandler(oAuth2AuthenticationSuccessHandler)
                .failureHandler(oAuth2AuthenticationFailureHandler);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("http://localhost:3000", "http://44.206.171.242:8080"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}