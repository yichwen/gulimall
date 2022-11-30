package io.dao.gulimall.order.config;

import com.rabbitmq.client.Channel;
import io.dao.gulimall.order.entity.OrderEntity;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 如果RabbitMQ没有这些组件，容器中的组件将会自动创建（只有当有设置监听器时，才会创建！！）
 * 一旦已创建，属性发生变化也不会覆盖！！
 */
@Configuration
public class MyMQConfig {

    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange", "order-event-exchange");
        args.put("x-dead-letter-routing-key", "order.release.order");
        args.put("x-message-ttl", 60000);
        return new Queue(
                "order.delay.queue",
                true,
                false,
                false,
                args
        );
    }

    @Bean
    public Queue orderReleaseOrderQueue() {
        return new Queue(
                "order.release.order.queue",
                true,
                false,
                false
        );
    }

    @Bean
    public Exchange orderEventExchange() {
        return new TopicExchange(
                "order-event-exchange",
                true,
                false
        );
    }

    @Bean
    public Binding orderCreateOrderBinding() {
        return new Binding(
                "order.delay.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.create.order",
                null
        );
    }

    @Bean
    public Binding orderReleaseOrderBinding() {
        return new Binding(
                "order.release.order.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.order",
                null
        );
    }

    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding(
                "stock.release.stock.queue",
                Binding.DestinationType.QUEUE,
                "order-event-exchange",
                "order.release.other.#",
                null
        );
    }


}
