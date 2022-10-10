package com.balawo.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * @author yan
 * @date 2022-10-08
 * spring security 基础配置
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
//只有加了@EnableGlobalMethodSecurity(prePostEnabled=true) 那么在方法上面使用的 @PreAuthorize(“hasAuthority(‘admin’)”)才会生效
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * 密码编辑器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置认证管理器，oauth2需要配置这个支持password模式
     * 方法名必须是 authenticationManagerBean，否则会报错Handling error: NestedServletException nested exception is java.lang.StackOverflowError
     * @return
     */
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();// 解决不允许显示在iframe的问题
        http.headers().frameOptions().disable().cacheControl();
        http.authorizeRequests()//开启配置
                .antMatchers( "/auth/**")
                .permitAll()//不验证，直接放行
                .anyRequest()//其他请求
                .authenticated()//验证   表示其他请求只需要登录就能访问
                .and()
                .formLogin();//允许表单登录

    }
}

