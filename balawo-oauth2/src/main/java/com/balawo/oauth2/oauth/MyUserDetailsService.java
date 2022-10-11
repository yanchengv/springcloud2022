package com.balawo.oauth2.oauth;


import com.balawo.common.model.LoginUser;
import com.balawo.oauth2.service.OauthUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author yan
 * @date 2022-10-08
 */
@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    private OauthUserDetailsService oauthUserDetailsService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LoginUser userDetails = oauthUserDetailsService.loadUserByUsername(username);
        return userDetails;

    }
}

