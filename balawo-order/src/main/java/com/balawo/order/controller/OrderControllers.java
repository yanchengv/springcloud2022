package com.balawo.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yan
 * @date 2022-10-01
 */
@RestController
@RequestMapping("/orders")
public class OrderControllers {

    @GetMapping("/index")
    public String index (){
        return "订单列表";
    }
}
