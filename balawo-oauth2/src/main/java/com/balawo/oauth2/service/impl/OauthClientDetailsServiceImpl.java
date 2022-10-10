package com.balawo.oauth2.service.impl;

import com.balawo.oauth2.service.OauthClientDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author yan
 * @date 2022-10-08
 */
@Service
public class OauthClientDetailsServiceImpl implements OauthClientDetailsService {



    @Override
    public BaseClientDetails getByClientId(String clientId) {
        BaseClientDetails client = new BaseClientDetails();
        //客户端ID
        client.setClientId("client_id1");
        //客户端秘钥
        client.setClientSecret(new BCryptPasswordEncoder().encode("client_secret1"));
        client.setAccessTokenValiditySeconds(3600); //配置访问token的有效期
        Set<String> authType = new HashSet<>();
        authType.add("password");
        authType.add("refresh_token");

        //配置grant_type 针对当前应用客户端：admin-client,所能支持的授权模式是哪些?总共5种(授权码模式：authorization_code;密码模式：password;客户端模式：client_credentials;简化模式：implicit;令牌刷新：refresh_token)。
        //配置grant_type 表示授权模式 授权码模式获取code: localhost:9003/oauth/authorize?response_type=password&client_id=admin-client
        client.setAuthorizedGrantTypes(authType);
        client.setResourceIds(Arrays.asList("admin"));
        //发出去的权限有哪些?之前前端请求携带了scope,此配置的scope用来指定前端发送scope的值必须在配置的里面或者不携带scope;默认为此处配置的scope
        client.setScope(Arrays.asList("all"));
        return client;
    }
}
