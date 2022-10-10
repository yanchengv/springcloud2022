package com.balawo.order.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author yan
 * @date 2022-10-01
 */
@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderControllers {

    @GetMapping("/index")
    @PreAuthorize("hasAuthority('admin')") //拥有admin权限的用户才能访问
    public String index (){
        return "订单列表";
    }

    @GetMapping("/myOrders")
    public String myOrders(){
        return "我的订单";
    }
}
