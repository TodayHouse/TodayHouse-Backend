package com.todayhouse.domain.user.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuthProvider authProvider;

    @Column(length = 50, unique = true)
    private String email;

    @Column(length = 200)
    private String password;

    @Column(length = 15, unique = true)
    private String nickname;

    private boolean agreeTOS;
    private boolean agreePICU;
    private boolean agreePromotion;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
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

    public User update(String name) {
        this.email = name;
        return this;
    }

    public List<String> getRoleKey() {
        return roles;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", authProvider=" + authProvider +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", nickname='" + nickname + '\'' +
                ", agreeTOS=" + agreeTOS +
                ", agreePICU=" + agreePICU +
                ", agreePromotion=" + agreePromotion +
                ", roles=" + roles +
                '}';
    }
}
