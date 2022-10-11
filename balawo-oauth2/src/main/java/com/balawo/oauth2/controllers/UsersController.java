package com.balawo.oauth2.controllers;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yan
 * @date 2022-10-08
 */
@Slf4j
@RestController
@RequestMapping("/users")
public class UsersController {

    @GetMapping("/index")
    @PreAuthorize("hasAuthority('admin')")
    public String index(){
        return "users index";
    }
}
