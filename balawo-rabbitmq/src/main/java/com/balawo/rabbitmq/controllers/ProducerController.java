package com.balawo.rabbitmq.controllers;

import com.balawo.rabbitmq.config.MqConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * 消息生产者
 *
 * @author yan
 * @date 2022-12-28
 */
@RestController
@RequestMapping("/produce")
@Slf4j
public class ProducerController {

    @Autowired
    @Resource(name = "rabbitTemplate")
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/create")
    public String create() {
        //1、发送及时消息
        Map<String, String> notice = new HashMap<>();
        notice.put("张三", "6岁");
        notice.put("李四", "5岁");
        rabbitTemplate.convertAndSend(MqConfig.COMMENT_EXCHANGE, MqConfig.COMMENT_ROUTE_KEY, notice);

        //2、使用死信延迟消息5秒后发送自动过审消息通知
        rabbitTemplate.convertAndSend(MqConfig.COMMENT_CHECK_SUCCESS_EXCHANGE, MqConfig.COMMENT_CHECK_SUCCESS_ROUTE_KEY, "评论自动审核成功，死信延迟消息5秒！！！");

        //3、使用插件延迟订单取消消息
        rabbitTemplate.convertAndSend(MqConfig.ORDER_EXCHANGE,MqConfig.ORDER_CANCEL_ROUTE_KEY,"订单取消：orderId123",message -> {
            //注意这里时间毫秒可以使long，而且是设置header
            message.getMessageProperties().setHeader("x-delay", 7000);
            return message;
        });
        //使用插件延迟订单支付消息
        rabbitTemplate.convertAndSend(MqConfig.ORDER_EXCHANGE,MqConfig.ORDER_PAY_ROUTE_KEY,"订单支付：orderId124",message -> {
            message.getMessageProperties().setHeader("x-delay",6000);
            return message;
        });

        return "评论创建成功";
    }

}
