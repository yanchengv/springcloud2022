package com.balawo.oauth2.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yan
 * @date 2022-10-08
 */
@RestController
@RequestMapping("/users")
public class UsersController {
    @GetMapping("/index")
    public String index(){
        return "balawo-oauth2 usersController index demo";
    }
}
