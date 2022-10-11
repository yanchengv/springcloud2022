package com.balawo.oauth2.service;

import com.balawo.common.model.LoginUser;

/**
 * @author yan
 * @date 2022-10-08
 */
public interface OauthUserDetailsService {

    LoginUser loadUserByUsername(String username);
}
