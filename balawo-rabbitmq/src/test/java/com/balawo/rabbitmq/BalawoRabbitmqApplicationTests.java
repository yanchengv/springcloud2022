package com.balawo.rabbitmq;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BalawoRabbitmqApplicationTests {

    @Test
    public void contextLoads() {

    }



    @Test
    public void rabbitmqTest() throws InterruptedException {
//        producer.produce();
//        Thread.sleep(4000);
        long limitTime = 86400000L - 1L;
        System.out.println(limitTime);
    }

}
