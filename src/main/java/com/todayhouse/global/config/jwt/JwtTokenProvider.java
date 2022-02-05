package com.todayhouse.global.config.jwt;

import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.global.config.oauth.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final UserDetailsService userDetailsService;

    @Value("${jwt.tokenSecret}")
    private String secretKey;

    @Value("${jwt.tokenExpirationMsec}")
    private Long expiration;

    // 객체 초기화, secretKey를 Base64로 인코딩한다.
    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createToken(String userPk, List<Role> roles) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("roles", roles); // 정보는 key / value 쌍으로 저장된다.
        Date now = new Date();
        Optional<Role> roleGuest = roles.stream().filter(r -> r.equals(Role.GUEST)).findFirst();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + (roleGuest.isPresent() ? 5 * 60 * 1000 : expiration))) // set Expire Time, guest는 3분
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘, secret 값
                .compact();
    }

    // 인증 성공시 JWT 토큰에서 인증 정보 조회, SecurityContextHolder에 저장할 객체 생성
    public Authentication getAuthentication(String token) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // jwt 를 추출한다.
    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        //Authorization : Bearer {토큰} 방식으로 헤더를 받는다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer "))
            return request.getHeader("Authorization").substring(7);
        //auth_user cookie 에서 토큰을 추출한다.
        return CookieUtils.getCookie(request, "auth_user")
                .map(cookie -> CookieUtils.deserialize(cookie, String.class))
                .orElse(null);
    }

    // 토큰의 유효성 + 만료일자 확인
    public boolean validateToken(String jwtToken) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey)
                    .parseClaimsJws(jwtToken);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}

