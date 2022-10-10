package com.balawo.oauth2.oauth;

import com.balawo.oauth2.service.OauthClientDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

/**
 * @author yan
 * @date 2022-10-08
 */
public class MyClientDetailsService implements ClientDetailsService {
    Logger logger = LoggerFactory.getLogger(MyUserDetailsService.class);

    @Autowired
    private OauthClientDetailsService oauthClientDetailsService;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        //查询数据库获取客户端用户信息
        BaseClientDetails baseClientDetails =  oauthClientDetailsService.getByClientId(clientId);
        return baseClientDetails;
    }
}

