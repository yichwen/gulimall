package io.dao.gulimall.order.controller;

import io.dao.gulimall.order.entity.OrderEntity;
import io.dao.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.UUID;

@Slf4j
@RestController
public class RabbitController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/sendmq")
    public String sendMQ(@RequestParam(required = false, defaultValue = "10") Integer num) {
        for (int i = 0; i < num; i++) {
            int mod = i % 2;
            if (mod == 0) {
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId(1L);
                orderReturnReasonEntity.setCreateTime(new Date());
                orderReturnReasonEntity.setName("哈哈");
                // 以 hello.java 的路由键 发送消息 Hello World 到 hello-java-exchange
                String message = "Hello World";
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity,
                        new CorrelationData(UUID.randomUUID().toString()));

            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity,
                        new CorrelationData(UUID.randomUUID().toString()));
            }
            log.info("消息发送完成");
        }
        return "ok";
    }
}
