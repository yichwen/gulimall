package io.dao.gulimall.order;

import io.dao.gulimall.order.entity.OrderEntity;
import io.dao.gulimall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.UUID;

@Slf4j
@SpringBootTest
class GulimallOrderApplicationTests {

	@Autowired
	private AmqpAdmin amqpAdmin;

	@Autowired
	private RabbitTemplate rabbitTemplate;

	/**
	 * 如何创建 Exchange, Queue, Binding？
	 * 	- 使用 AmqpAdmin 进行创建
	 * 如何收发消息？
     *  - 使用 RabbitTemplate 进行发送
	 */

	@Test
	void sendMessageTest() {
		// 如果发送的消息是个对象，会使用序列化机制，将对象写出去，对象必须实现 Serializable (如果是使用默认的序列化机制)
		// 可以使用自定义序列化机制，将一个 MessageConverter 的实例注入到容器中 e.g. Jackson2JsonMessageConverter 是 JSON 的序列化机制
		for (int i = 0; i < 10; i++) {
			int mod = i % 2;
			if (mod == 0) {
				OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
				orderReturnReasonEntity.setId(1L);
				orderReturnReasonEntity.setCreateTime(new Date());
				orderReturnReasonEntity.setName("哈哈");
				// 以 hello.java 的路由键 发送消息 Hello World 到 hello-java-exchange
				String message = "Hello World";
				rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity);

			} else {
				OrderEntity orderEntity = new OrderEntity();
				orderEntity.setOrderSn(UUID.randomUUID().toString());
				rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderEntity);
			}
			log.info("消息发送完成");
		}
	}


	@Test
	void createExchange() {
		// Exchange
		DirectExchange directExchange = new DirectExchange(
				"hello-java-exchange",
				true,
				false);
		amqpAdmin.declareExchange(directExchange);
		log.info("Exchange[hello-java-exchange]创建成功");
	}

	@Test
	void createQueue() {
		Queue queue = new Queue(
				"hello-java-queue",
				true,
				false,
				false);
		amqpAdmin.declareQueue(queue);
		log.info("Queue[hello-java-queue]创建成功");
	}

	@Test
	void createBinding() {
		// 交换器可以和交换器/队列绑定
		Binding binding = new Binding(
				"hello-java-queue",		// 交换器要绑定的目的地
				Binding.DestinationType.QUEUE,		// 交换器要绑定的目的地类型
				"hello-java-exchange",	// 指定交换器名称，和目的地绑定
				"hello.java",			// 路由键
				null
		);
		amqpAdmin.declareBinding(binding);
		log.info("Binding[hello-java-binding]创建成功");
	}
}
