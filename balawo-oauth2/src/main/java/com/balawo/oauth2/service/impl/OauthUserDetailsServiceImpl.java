package com.balawo.oauth2.service.impl;

import com.balawo.oauth2.service.OauthUserDetailsService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author yan
 * @date 2022-10-08
 * 用户名 u1，密码p1，权限admin,read_auth
 * 请求地址：http://localhost:9002/oauth/token。body中携带参数 grant_type=password&client_id=client_id1&client_secret=client_secret1&scope=all&username=u1&password=p1 就可以访问到token
 */
@Service
public class OauthUserDetailsServiceImpl implements OauthUserDetailsService {
    @Override
    public User loadUserByUsername(String username) {
        //查询登录用户信息
        String name = "u1";
        String password = new BCryptPasswordEncoder().encode("p1");
        List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("admin,read_auth");
        //此处的User是org.springframework.security.core.userdetails.User。也可以自定义User
        User user = new User(name,password,auths);
        return user;
    }
}
