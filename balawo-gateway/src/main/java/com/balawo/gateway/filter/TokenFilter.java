package com.balawo.gateway.filter;

import com.alibaba.fastjson.JSON;
import com.balawo.common.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Token过滤，完成当前登录用户信息提取，并放入转发微服务的request中
 *
 * @author yan
 * @date 2022-10-10
 */
@Slf4j
@Component
public class TokenFilter implements GlobalFilter, Ordered {
    @Autowired
    TokenStore tokenStore;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> authHeader = exchange.getRequest().getHeaders().get("Authorization");
        String token = null;
        if (authHeader != null) {
            String tokenValue = authHeader.get(0);
            tokenValue = tokenValue.replace("Bearer", "").trim();
            //返回base64编码的token(包含用户和权限信息等)
            token = buildToken(tokenValue);
        }
        if (token != null) {
            // 定义新的消息头
            HttpHeaders headers = new HttpHeaders();
            //把包含用户信息的base64的token放入header中
            headers.put("json-token", Collections.singletonList(token));
            headers.putAll(exchange.getRequest().getHeaders());
            //移除原来的token
            headers.remove("Authorization");
            ServerHttpRequest host = new ServerHttpRequestDecorator(exchange.getRequest()) {
                @Override
                public HttpHeaders getHeaders() {
                    return headers;
                }
            };
            //将现在的request 变成 change对象
            ServerWebExchange build = exchange.mutate().request(host).build();
            return chain.filter(build).then(Mono.fromRunnable(() -> {
                //log.info(" 后置 : " +exchange.getResponse().getStatusCode() + "\t"+ exchange.getRequest().getURI().toString());
            }));
        } else {
            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                //log.info(" 后置 : " + exchange.getResponse().getStatusCode() + "\t"+ exchange.getRequest().getURI().toString());
            }));
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }


    /**
     * redis token 转发明文给微服务
     *
     * @return
     */
    private String buildToken(String token) {

        OAuth2Authentication auth2Authentication = tokenStore.readAuthentication(token);
        if (auth2Authentication == null) {
            return null;
        }
        //String clientId = auth2Authentication.getOAuth2Request().getClientId();
        Authentication userAuthentication = auth2Authentication.getUserAuthentication();
        String userInfoStr = null;
        List<String> authorities = new ArrayList<>();
        if (userAuthentication != null) {
            log.info("getPrincipal===={}",userAuthentication.getPrincipal());
            LoginUser user = JSON.parseObject(JSON.toJSONString(userAuthentication.getPrincipal()),LoginUser.class);
            userInfoStr = JSON.toJSONString(user);
            // 组装明文token，转发给微服务，放入header，名称为json-token
            userAuthentication.getAuthorities().stream().forEach(
                    s -> authorities.add(((GrantedAuthority) s).getAuthority())
            );
        }

        OAuth2Request oAuth2Request = auth2Authentication.getOAuth2Request();
        Map<String, String> requestParams = oAuth2Request.getRequestParameters();
        Map<String, Object> jsonToken = new HashMap<>(requestParams);
        jsonToken.put("principal", userInfoStr);
        jsonToken.put("authorities", authorities);
        //把用户和权限进行base64编码
        return Base64.getEncoder().encodeToString(JSON.toJSONString(jsonToken).getBytes(StandardCharsets.UTF_8));
    }
}
