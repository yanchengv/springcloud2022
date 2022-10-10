package com.balawo.gateway.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * @author yan
 * @date 2022-10-10
 */
@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig {
    private final TokenStore tokenStore; //令牌存储，放在redis中

    public WebSecurityConfig(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }


    /**
     * 安全验证机制
     */

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) throws Exception {
        Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);
        logger.info("网关 WebSecurityConfig......");
        http
                .authorizeExchange()
                //获取令牌token的服务接口全部放行,/oauth2/** 开头的请求全部放行
                .pathMatchers("/oauth2/**").permitAll()
                //权限认证：不同微服务接口的访问权限不同
                //使用AuthorizationManager方法校验当前的用户token是否合法并且是否有scope=all的权限，如果有才能访问对应微服务路径 例如：/orders/**
                .pathMatchers("/order/**").access(new AuthorizationManager(tokenStore,"all"))
                .anyExchange().permitAll()
                .and().exceptionHandling()
                .and().csrf().disable();

        return http.build();
    }
}
