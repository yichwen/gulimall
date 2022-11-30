package io.dao.gulimall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 使用 RabbitMQ
 * - 引入 spring-boot-starter-amqp 依赖：RabbitMQAutoConfiguration 会自动生效
 * - 给容器中自动配置了 RabbitTemplate, AmqpAdmin, CachingConnectionFactory, RabbitMessagingTemplate
 * - 配置 RabbitMQ 信息 （所有的属性都是在 RabbitProperties "spring.rabbitmq.xxx"）
 * - @EnableRabbit 开启功能
 * - @RabbitListener 监听消息，必须开启功能（@EnableRabbit）才能使用
 * 		- 可以标注在 类+方法（监听哪些队列即可）
 * - @RabbitHandler 只能标注在方法上（可以重载区分不同的消息）
 */
@EnableRabbit
@EnableDiscoveryClient
@EnableFeignClients
@EnableRedisHttpSession
@SpringBootApplication
public class GulimallOrderApplication {

	public static void main(String[] args) {
		SpringApplication.run(GulimallOrderApplication.class, args);
	}

}
