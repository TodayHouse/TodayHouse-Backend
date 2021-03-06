package com.todayhouse.domain.user.oauth.application;

import com.todayhouse.domain.user.dao.UserRepository;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import com.todayhouse.domain.user.oauth.dto.OAuthAttributes;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        //로그인 서비스 구분
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        //로그인 서비스 별로 필요한 필드값이 다르므로 그 이름을 받아온다.(구글은 sub, 네이버는 id)
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint()
                .getUserNameAttributeName();
        //OAuth2UserService를 통해 가져온 OAuth2User의 attribute를 담는다.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        attributes.getAttributes().put("authProvider", attributes.getAuthProvider());
        attributes.getAttributes().put("houseId", user.getId());

        if (user.getAuthProvider() != null) // success handler에서 회원 가입 유무 판별을 위함
            attributes.getAttributes().put("signupProvider", user.getAuthProvider());

        Set<GrantedAuthority> authorities = transRoleListToGrantedAuthoritySet(user.getRoles());

        return new DefaultOAuth2User(
                authorities, attributes.getAttributes(), attributes.getNameAttributeKey());
    }

    //사용자 정보 업데이트시 반영
    private User saveOrUpdate(OAuthAttributes attributes) {
        User user = userRepository.findByEmail(attributes.getEmail())
                .orElse(attributes.toEntity());
        return userRepository.save(user);
    }

    //role list를 GrantedAuthority set으로 변환
    private Set<GrantedAuthority> transRoleListToGrantedAuthoritySet(List<Role> roles) {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getKey()))
                .collect(Collectors.toSet());
    }
}

