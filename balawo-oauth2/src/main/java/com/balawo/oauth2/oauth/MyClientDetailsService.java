package com.balawo.oauth2.oauth;

import com.balawo.oauth2.service.OauthClientDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * @author yan
 * @date 2022-10-08
 */
@Slf4j
public class MyClientDetailsService implements ClientDetailsService {
    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        //查询数据库获取客户端用户信息
        BaseClientDetails baseClientDetails =  oauthClientDetailsService.getByClientId(clientId);
        log.info("ClientDetails=========={}",baseClientDetails);
        return baseClientDetails;
    }
}

