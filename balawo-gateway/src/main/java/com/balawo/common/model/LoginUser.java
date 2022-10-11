package com.balawo.common.model;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/**
 * @author yan
 * @date 2022-10-11
 */
@Data
public class LoginUser implements UserDetails {
    private Integer id;
    private String username;
    private String password;
    private String mobile;
    private String email;
    private  Collection<GrantedAuthority> authorities;

    public LoginUser() {
    }

    public LoginUser(Integer id, String username, String password, String mobile, String email, Collection<GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mobile = mobile;
        this.email = email;
        this.authorities = authorities;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.authorities;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
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
}
