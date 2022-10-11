package com.balawo.oauth2.config;

import com.balawo.oauth2.oauth.MyClientDetailsService;
import com.balawo.oauth2.oauth.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenStore;

/**
 * @author yan
 * @date 2022-10-08
 * 提供/oauth/authorize,/oauth/token,/oauth/check_token,/oauth/confirm_access,/oauth/error
 */
@Configuration
@EnableAuthorizationServer
public class AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    Logger logger = LoggerFactory.getLogger(AuthorizationServerConfig.class);


    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenStore tokenStore;

    @Autowired
    MyUserDetailsService userDetailsService;

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        //表示支持 client_id和client_secret做登录认证
        security.allowFormAuthenticationForClients();
        security.tokenKeyAccess("permitAll()");//开启oauth/token_key验证端口认证权限访问
        security.checkTokenAccess("permitAll()");//oauth/check_token验证端口认证权限访问;
    }

    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(clientDetailsService());
    }

    /**
     * 令牌访问端点的配置：endpoints
     * 1.使用我们自己的授权管理器(AuthenticationManager)和自定义的用户详情服务(UserDetailsService)
     * 配置了密码模式所需要的AuthenticationManager
     * 配置了令牌管理服务，AuthorizationServerTokenServices
     * 配置/oauth/token申请令牌的uri只允许POST提交。
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //配置密码模式 authenticationManager
        endpoints.authenticationManager(authenticationManager)
                 .userDetailsService(userDetailsService) //自定义的用户详情服务
                //令牌管理服务，无论哪种模式都需要
                .tokenServices(tokenServices());

    }

    /**
     * 令牌服务的配置
     */
    @Bean
    public AuthorizationServerTokenServices tokenServices() {
        DefaultTokenServices services = new DefaultTokenServices();
        //支持令牌的刷新
        services.setSupportRefreshToken(true);
        //令牌服务
        services.setTokenStore(tokenStore);
        //access_token的过期时间 2小时
        services.setAccessTokenValiditySeconds(60 * 60 * 2);
        //refresh_token的过期时间
        services.setRefreshTokenValiditySeconds(60 * 60 * 24 * 3);
        return services;
    }

    @Bean
    public ClientDetailsService clientDetailsService(){
        MyClientDetailsService myClientDetailsService = new MyClientDetailsService();
        return myClientDetailsService;
    }

}





