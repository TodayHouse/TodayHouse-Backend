package com.todayhouse.domain.user.oauth.dto;

import com.todayhouse.domain.user.domain.AuthProvider;
import com.todayhouse.domain.user.domain.Role;
import com.todayhouse.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
import java.util.Map;

@Builder
@Getter
@AllArgsConstructor
public class OAuthAttributes {
    protected Map<String, Object> attributes;
    protected String nameAttributeKey;
    protected AuthProvider authProvider;
    protected String name;
    protected String email;
    protected String picture;

    // 리소스 서버에서 받은 정보 추출
    public static OAuthAttributes of(String registrationId, String userNameAttributeName,
                                     Map<String, Object> attributes){
//        if(registrationId.equals("naver"))
        return ofNaver("id",attributes);
    }

//    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
//        return OAuthAttributes.builder()
//                .name((String) attributes.get("name"))
//                .email((String) attributes.get("email"))
//                .picture((String) attributes.get("picture"))
//                .attributes(attributes)
//                .nameAttributeKey(userNameAttributeName)
//                .build();
//    }

    private static OAuthAttributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");
        return OAuthAttributes.builder()
                .authProvider(AuthProvider.naver)
                .name((String) response.get("name"))
                .email((String) response.get("email"))
                .picture((String) response.get("profile_image"))
                .attributes(response)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity(){
        return User.builder()
                .authProvider(authProvider)
                .email(email)
                .roles(Collections.singletonList(Role.USER.getKey()))
                .signedUp(false)
                .build();
    }

    @Override
    public String toString() {
        return "OAuthAttributes{" +
                "attributes=" + attributes +
                ", nameAttributeKey='" + nameAttributeKey + '\'' +
                ", authProvider=" + authProvider +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }
}
