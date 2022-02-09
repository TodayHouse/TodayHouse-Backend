package com.todayhouse.global.config.jwt;

import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.exception.InvalidAuthException;
import com.todayhouse.global.config.oauth.CookieUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    @Value("${jwt.tokenSecret}")
    private String secretKey;

    @Value("${jwt.tokenExpirationMsec}")
    private Long expiration;

    @Value("${jwt.guestTokenExpirationMsec}")
    private Long guestExpiration;

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
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘, secret 값
                .compact();
    }

    // JWT guest 토큰 생성
    public String createOAuthToken(String userPk, List<Role> roles, AuthProvider authProvider, String profileImage, String nickname) {
        Claims claims = Jwts.claims().setSubject(userPk); // JWT payload 에 저장되는 정보단위
        claims.put("authProvide", authProvider);
        claims.put("roles", roles);
        if (profileImage != null)
            claims.put("profileImage", profileImage);
        if (nickname != null)
            claims.put("nickname", nickname);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims) // 정보 저장
                .setIssuedAt(now) // 토큰 발행 시간 정보
                .setExpiration(new Date(now.getTime() + guestExpiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)  // 사용할 암호화 알고리즘, secret 값
                .compact();
    }

    // 인증 성공시 JWT 토큰에서 인증 정보(payload) 조회, SecurityContextHolder에 저장할 객체 생성
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody();

        if (claims.get("roles") == null) {
            throw new InvalidAuthException();
        }
        // 클레임에서 권한 정보 가져오기
        String roleStr = claims.get("roles").toString();
        List<Role> roles =
                Arrays.stream(roleStr.substring(1, roleStr.length() - 1).split(", "))
                        .map(role -> Role.valueOf(role))
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = User.builder()
                .authProvider(claims.get("authProvide")!=null?AuthProvider.valueOf(claims.get("authProvide").toString()):null)
                .email(claims.getSubject())
                .nickname((String) claims.get("nickname"))
                .profileImage((String) claims.get("profileImage"))
                .roles(roles)
                .build();

        return new UsernamePasswordAuthenticationToken(principal, "", principal.getAuthorities());
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

