package com.balawo.oauth2.service;

import org.springframework.security.core.userdetails.User;

/**
 * @author yan
 * @date 2022-10-08
 */
public interface OauthUserDetailsService {

    User loadUserByUsername(String username);
}
