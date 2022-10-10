package com.balawo.order.filter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Base64;

/**
 * 微服务定义filter拦截并解析从网关发送的token，并形成Spring Security的Authentication对象
 *
 * @author yan
 * @date 2022-10-10
 */
@Slf4j
@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        //token中包含当前登录用户和权限的信息 使用base64编码
        String token = httpServletRequest.getHeader("json-token");
//        log.info("TokenAuthenticationFilter==============:{}",token);
        token = token.replace("Bearer", "").trim();
        if (token != null) {
            //把base64的token解码
            String userJson = new String(Base64.getDecoder().decode(token));
            //userJson转换成json对象
            JSONObject userJsonObj = JSON.parseObject(userJson);
            //客户端模式没经过用户名密码登录，所以得不到User信息
            if (!"client_credentials".equals(userJsonObj.getString("grant_type"))) {
                //获取当前用户信息
                String principal = userJsonObj.getString("principal");
                //转换为loginUser
                User loginUser = JSON.parseObject(principal, User.class);
                JSONArray authoritiesArray = userJsonObj.getJSONArray("authorities");
                String[] authorities = authoritiesArray.toArray(new String[authoritiesArray.size()]);
                //形成Spring Security的Authentication对象，这样可在项目方法中使用权限校验和获取当前登录用
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginUser, null, AuthorityUtils.createAuthorityList(authorities));
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
