package com.balawo.rabbitmq.config;

import org.springframework.amqp.core.*;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yan
 * @date 2022-12-15
 * 自动创建 定义了交换机名称、队列名称、路由键名称。绑定交换机和队列以及路由键
 */

@Configuration
//@ConditionalOnExpression("${sys.config.rabbitmq.enabled:false}")
public class MqConfig {
    @Value("${spring.rabbitmq.host}")
    private String host;
    @Value("${spring.rabbitmq.username}")
    private String username;
    @Value("${spring.rabbitmq.password}")
    private String password;
    @Value(("${spring.rabbitmq.virtualHost}"))
    private String virtualHost;

    //设置comment队列名称
    public static final String COMMENT_EXCHANGE = "comment_exchange";
    public static final String COMMENT_QUEUE = "comment_queue";
    public static final String COMMENT_ROUTE_KEY = "comment_route_key";

    /**
     *
     * 消息延迟推送两种解决方案
     * https://blog.csdn.net/Wu_Shang001/article/details/120604882
     * 一、死信消息（队列ttl+死信exchange）
     * 简述：使用两个队列，一个队列接收消息不消费，等待指定时间后消息死亡，再由该队列绑定的死信exchange再次将其路由到另一个队列提供业务消费。
     * (1)先声明一个消费队列 queue_dlx，用来接收死信消息，并提供消费；
     * (2)然后声明一个死信exchange_dlx, 绑定 queue_dlx，接收消息后路由至queue_dlx；
     * (3)声明一个延迟队列，queue_delay, 用来接收业务消息，但不提供消费，等待消息死亡后转至死信exchange。（即延迟）
     * (4)声明一个exchange，由业务发送消息到exchange，然后转至queue_delay
     *
     * 二、延时插件 (rabbitmq-delayed-message-exchange)
     */

     //一、死信实现延迟消息
     public static final String  DEAD_COMMENT_EXCHANGE = "dead_exchange";
     public static final String  DEAD_COMMENT_QUEUE = "dead_queue";
     public static final String  DEAD_COMMENT_ROUTE_KEY = "dead_route_key";
     //评论自动过审通知消息
     public static final String COMMENT_CHECK_SUCCESS_EXCHANGE = "comment_check_success_exchange";
     public static final String COMMENT_CHECK_SUCCESS_QUEUE = "comment_check_success_queue";
     public static final String COMMENT_CHECK_SUCCESS_ROUTE_KEY = "comment_check_success_route_key";


    /**
     * 二、延时插件实现延迟消息 (rabbitmq-delayed-message-exchange)
     */
    public static final String ORDER_EXCHANGE = "order_exchange";
    public static final String ORDER_CANCEL_ROUTE_KEY = "order_cancel_route_key";
    public static final String ORDER_CANCEL_QUEUE = "order_cancel_queue";
    public static final String ORDER_PAY_ROUTE_KEY = "order_pay_route_key";
    public static final String ORDER_PAY_QUEUE = "order_pay_queue";


    //生产者发送消息配置 对mq里面对象进行json序列化和反序列化
    @Bean(name = "rabbitTemplate")
    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory")ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        rabbitTemplate.setMandatory(true);
        return rabbitTemplate;
    }

    //消费者消费消息配置
    @Bean("rabbitListenerContainerFactory")
    //@Primary //如果是有多个rabbitmq 需要指定
    public RabbitListenerContainerFactory<?> rabbitListenerContainerFactory(@Qualifier("connectionFactory")ConnectionFactory connectionFactory){
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        //设置手动签收消息
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setPrefetchCount(10);
        return factory;
    }

    //连接 RabbitMQ
    @Bean(name = "connectionFactory")
    //@Primary //如果是有多个rabbitmq 需要指定
    public ConnectionFactory connectionFactory () {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory();
        //设置服务地址
        connectionFactory.setHost(host);
        //设定端口，注意，这里RabbitMQ的端口，不是管理页面的端口
        connectionFactory.setPort(5672);
        //设定用户名
        connectionFactory.setUsername(username);
        //设定密码
        connectionFactory.setPassword(password);
        //设定虚拟访问节点
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(true);
        connectionFactory.setPublisherReturns(true);
        return connectionFactory;
    }


    //创建评论 持久化的交换机
    @Bean
    public Exchange commentExchange() {
        return ExchangeBuilder.directExchange(COMMENT_EXCHANGE).durable(true).build();
    }
    //创建评论持久化队列
    @Bean
    public Queue commentQueue(){
        return new Queue(COMMENT_QUEUE,true);
    }
    /**
     * 绑定评论队列到评论的交换机上面
     * @return
     */
    @Bean
    public Binding commentBind(){
        return BindingBuilder.bind(commentQueue()).to(commentExchange()).with(COMMENT_ROUTE_KEY).noargs();
        //return null;
    }

    // --------------------- 使用死信实现延时队列 --------------------------


    //创建评论自动过审成功交换机
    @Bean
    public Exchange commentCheckSuccessExchange(){return ExchangeBuilder.directExchange(COMMENT_CHECK_SUCCESS_EXCHANGE).durable(true).build();};
    //创建评论自动审核成功的队列，并指定死信交换机
    @Bean
    public Queue commentCheckSuccessQueue(){
        Map<String,Object> map = new HashMap<>();
        //设置消息的过期时间 单位毫秒
        //map.put("x-message-ttl", 60*60000*48);//60*60000*48 48h
        map.put("x-message-ttl", 5000);//60*60000*48 48h
        //设置附带的死信交换机
        map.put("x-dead-letter-exchange",DEAD_COMMENT_EXCHANGE);
        //指定重定向的路由建 消息作废之后可以决定需不需要更改他的路由建 如果需要 就在这里指定
        map.put("x-dead-letter-routing-key",DEAD_COMMENT_ROUTE_KEY);
        Queue queue = new Queue(COMMENT_CHECK_SUCCESS_QUEUE, true, false, false, map);
        return queue;
    };
    @Bean
    public Binding commentCheckSuccessBind(){
        return BindingBuilder.bind(commentCheckSuccessQueue()).to(commentCheckSuccessExchange()).with(COMMENT_CHECK_SUCCESS_ROUTE_KEY).noargs();
    }
    //创建死信交换机
    @Bean
    public Exchange deadCommentExchange(){return ExchangeBuilder.directExchange(DEAD_COMMENT_EXCHANGE).durable(true).build();};
    //创建死信队列，消息成为死信消息后放入该队列
    @Bean
    public Queue deadCommentQueue(){return new Queue(DEAD_COMMENT_QUEUE,true);}
    //绑定死信队列和死信交换机
    @Bean
    public Binding deadCommentBind(){
        return BindingBuilder.bind(deadCommentQueue()).to(deadCommentExchange()).with(DEAD_COMMENT_ROUTE_KEY).noargs();
    }


    // --------------------- 使用插件实现延时队列 --------------------------

    /**
     * 插件实现延时队列交换机
     * 注意这里的交换机类型：CustomExchange
     *  CustomExchange自定义交换机，可以自己定义设置参数
     */
    @Bean
    public CustomExchange orderExchange(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(ORDER_EXCHANGE,"x-delayed-message",true,false,args);
    }
    //声明订单取消队列
    @Bean
    public Queue orderCancelQueue(){return new Queue(ORDER_CANCEL_QUEUE,true);}
    @Bean
    public Binding orderCancelBind(){
        return BindingBuilder.bind(orderCancelQueue()).to(orderExchange()).with(ORDER_CANCEL_ROUTE_KEY).noargs();
    }

    //声明订单支付队列
    @Bean
    public Queue orderPayQueue(){return new Queue(ORDER_PAY_QUEUE,true);}
    @Bean
    public Binding orderPayBind(){
        return BindingBuilder.bind(orderPayQueue()).to(orderExchange()).with(ORDER_PAY_ROUTE_KEY).noargs();
    }

}
