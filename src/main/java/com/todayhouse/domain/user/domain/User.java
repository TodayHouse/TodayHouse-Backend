package com.todayhouse.domain.user.domain;

import com.todayhouse.domain.user.oauth.dto.request.OAuthSignupRequest;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "auth_provider")
    private AuthProvider authProvider;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 200)
    private String password;

    @Column(length = 15, unique = true)
    private String nickname;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private String birth;

    @Column(name = "profile_image")
    private String profileImage;

    @Embedded
    private Agreement agreement;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(joinColumns = @JoinColumn(name = "user_id"))
    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getKey()))
                .collect(Collectors.toList());
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public void updateWithOAuthSignup(OAuthSignupRequest request, User principal) {
        this.nickname = request.getNickname();
        this.roles = Collections.singletonList(Role.USER);
        this.agreement = Agreement.agreeAll();
        this.authProvider = principal.getAuthProvider();
        this.profileImage = principal.getProfileImage();
    }


}
