package com.balawo.oauth2.service;

import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * @author yan
 * @date 2022-10-08
 */
public interface OauthClientDetailsService {

    /**
     * 根据clientId查询客户端信息
     *
     */

    BaseClientDetails getByClientId(String clientId);

}
