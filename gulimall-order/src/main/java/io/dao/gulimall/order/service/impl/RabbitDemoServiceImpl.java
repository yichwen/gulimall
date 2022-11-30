package io.dao.gulimall.order.service.impl;

import com.rabbitmq.client.Channel;
import io.dao.gulimall.order.entity.OrderEntity;
import io.dao.gulimall.order.entity.OrderReturnReasonEntity;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * for demo
 */
@Service
@RabbitListener(queues = { "hello-java-queue" })
public class RabbitDemoServiceImpl {

    /**
     * queues 声明需要监听的队列
     * 参数可以是：
     * - Message 原生消息类型（头+体）(org.springframework.amqp.core.Message)
     * - <T> 发送消息的类型 e.g. OrderReturnReasonEntity
     * - Channel 当前传输数据的通道
     *
     * Queue：可以很多人来监听，只要收到消息，队列删除消息，而且只能有一个受到消息
     * 场景：
     *      1. 订单服务启动多个，同一个消息，只能一个客户端收到
     *      2. 只有一个消息完全处理完，方法运行结束，就可以接收下一个消息
     */
//    @RabbitListener(queues = { "hello-java-queue" })
    @RabbitHandler
    public void receiveMessage(Message message, OrderReturnReasonEntity content, Channel channel) {
        // 消息体
        byte[] body = message.getBody();
        // 消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        System.out.println("接收到消息...内容：" + message + "==> 类型：" + message.getClass());
        System.out.println("接收到消息...内容：" + content);
        // delivery tag 在通道内按顺序自增的
        long deliveryTag = messageProperties.getDeliveryTag();
        // 手动确认 multiple=false -> 非批量模式
        try {
            // 模拟测试签收和无签收的情况
            if (deliveryTag % 2 == 0) {
                channel.basicAck(deliveryTag, false);
                System.out.println("已签收的货物 > " + deliveryTag);
            } else {
                // long deliveryTag, boolean multiple, boolean requeue 是否重新放入队列 (false 丢弃)
                channel.basicNack(deliveryTag, false, true);
                // long deliveryTag, boolean requeue
//                channel.basicReject();
                System.out.println("没有签收的货物 > " + deliveryTag);
            }
        } catch (IOException e) {
            // 网络中断
            e.printStackTrace();
        }
    }

    public void receiveOrderEntity(OrderEntity content) {
        System.out.println("接收到消息...内容：" + content);
    }

}
