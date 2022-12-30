package com.balawo.rabbitmq.consumer;

import com.balawo.rabbitmq.config.MqConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

import static com.balawo.rabbitmq.config.MqConfig.*;

/**
 * @author yan
 * @date 2022-12-16
 * rabbitmq消费者
 */
@Component
@Slf4j
public class Consumer {
    /**
     * 接收实时消费队列
     * @param msg
     * @param channel
     * @param message
     */
    @RabbitHandler
    @RabbitListener(queues = COMMENT_QUEUE, containerFactory = "rabbitListenerContainerFactory")
    public void process(@Payload Map<String, String> msg, Channel channel, Message message) {
        log.info("创建评论后立即接收的成功通知消息.....");
        log.info("创建评论立即收到消息：==={}", msg);
        //手动接收
        long deliverytag = message.getMessageProperties().getDeliveryTag();
        ack(channel,deliverytag);
    }

    /**
     * 接收使用死信延迟消息
     * @param msg
     * @param channel
     * @param message
     */
    @RabbitHandler
    @RabbitListener(queues = {DEAD_COMMENT_QUEUE}, containerFactory = "rabbitListenerContainerFactory")
    public void commentAutoCheckSuccess(@Payload String msg, Channel channel, Message message) {
        log.info("评论自动过审的成功通知消息....");
        log.info("评论自动过审的消息====={}", msg);
        //手动接收
        long deliverytag = message.getMessageProperties().getDeliveryTag();
        ack(channel,deliverytag);
    }

    /**
     * 接收使用插件实现延迟的订单取消消息
     * @param channel
     */

    @RabbitHandler
    @RabbitListener(queues = {ORDER_CANCEL_QUEUE},containerFactory = "rabbitListenerContainerFactory")
    public void orderCancel(@Payload String msg, Channel channel, Message message){
        log.info("订单取消通知消息....");
        log.info("订单取消的消息====={}",msg);
        //手动接收
        long deliverytag = message.getMessageProperties().getDeliveryTag();
        ack(channel,deliverytag);
    }

    /**
     * 接收使用插件实现延迟订单支付的消息
     * @param channel
     */
    @RabbitListener(queues = ORDER_PAY_QUEUE,containerFactory = "rabbitListenerContainerFactory")
    public void orderPay(@Payload String msg,Channel channel,Message message){
        log.info("订单支付通知消息....");
        log.info("订单支付的消息====={}",msg);
        //手动接收
        long deliverytag = message.getMessageProperties().getDeliveryTag();
        ack(channel,deliverytag);
    }



    //手动签收消息
    public void ack(Channel channel, long deliveryTag){
        try {
            //手动签收消息,deliveryTag是消息的唯一标识
            /**
             * 例如：有值为5,6,7,8 deliveryTag的投递
             * 如果此时channel.basicAck(8, true);则表示前面未确认的5,6,7投递也一起确认处理完毕。
             * 如果此时channel.basicAck(8, false);则仅表示deliveryTag=8的消息已经成功处理。
             */
            channel.basicAck(deliveryTag, false);
        } catch (IOException e) {
            //当前消息签收失败，重新放入队列中，而不是丢弃或者成为死信消息
            /**
             * 如果channel.basicNack(8, true, true);表示deliveryTag=8之前未确认的消息都处理失败且将这些消息重新放回队列中。
             * 如果channel.basicNack(8, true, false);表示deliveryTag=8之前未确认的消息都处理失败且将这些消息直接丢弃。
             * 如果channel.basicNack(8, false, true);表示deliveryTag=8的消息处理失败且将该消息重新放回队列。
             * 如果channel.basicNack(8, false, false);表示deliveryTag=8的消息处理失败且将该消息直接丢弃。
             */
            try {
                channel.basicNack(deliveryTag, false, true);
            } catch (IOException f) {
                f.printStackTrace();
            }

        }

    }
}
