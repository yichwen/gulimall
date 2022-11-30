package io.dao.gulimall.order.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class MyRabbitConfig {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 定制 RabbitTemplate
     * 1. 服务器收到消息就回调
     *      - spring.rabbitmq.publisher-confirms=true
     *      - 设置确认回调 ConfirmCallback
     * 2. 消息正确抵达队列进行回调
     *      - spring.rabbitmq.publisher-returns=true
     *      - spring.rabbitmq.template.mandatory=true
     *      - 设置确认回调 ReturnCallback
     * 3. 消费端确认（保证每个消息被正确消费，此时才可以让broker删除这个消息）
     *      - 消费者收到消息，默认会自动 ack，消息就会从 broker 的 queue 中移除
     *          - 问题：收到很多消息，都自动发送 ack，只有一个消息处理成功后，宕机了，发生消息丢失
     *          - 解决：手动确认 spring.rabbitmq.listener.simple.acknowledge-mode=manual [auto|manual|none]
     *              - 只要我们没有明确告诉 mq，货物被签收（没有 ack），消息一直是 unacked，
     *              - 即使消费者宕机了，消息也不会丢失，会重新变为 ready
     *              - 一旦有新的消费者，就会发送给它
     *      - 如何签收：
     *          channel.basicAck(deliveryTag, false); 签收
     *          channel.basicNack(deliveryTag, false, true); 拒签
     *
     */
    @PostConstruct
    public void initRabbitTemplate() {
        // 设置确认回调
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
             * 只要消息抵达 broker 就 ack=true
             * @param correlationData 当前消息的唯一关联数据（消息的唯一id）
             * @param ack 消息是否成功收到
             * @param cause 失败的原因
             */
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                // 更新消息日志，服务器已收到
                System.out.println("confirm...correlationData[" + correlationData + "]==>ack[" + ack + "]==>cause[" + cause + "]");
            }
        });

        // 设置消息抵达队列的确认回调
        rabbitTemplate.setReturnCallback(new RabbitTemplate.ReturnCallback() {
            /**
             * 只要消息没有投递给指定队列，就触发这个失败回调
             * @param message 投递失败的消息详细信息
             * @param replyCode 回复的状态码
             * @param replyText 回复的文本内容跟
             * @param exchange 消息发送到的交换器
             * @param routingKey 消息发送使用的路由键
             */
            @Override
            public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
                // 报错，更新消息日志当前的消息状态（错误状态）
                System.out.println("failed...Message[" + message +
                        "]==>ack[" + replyCode +
                        "]==>replyText[" + replyText +
                        "]==>exchange[" + exchange +
                        "]==>routingKey[" + routingKey +
                        "]");
            }
        });
    }

}
