package io.dao.gulimall.ware.listener;

import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import io.dao.common.to.mq.OrderTo;
import io.dao.common.to.mq.StockDetailTo;
import io.dao.common.to.mq.StockLockedTo;
import io.dao.common.utils.R;
import io.dao.gulimall.ware.dao.WareSkuDao;
import io.dao.gulimall.ware.entity.WareOrderTaskDetailEntity;
import io.dao.gulimall.ware.entity.WareOrderTaskEntity;
import io.dao.gulimall.ware.feign.OrderFeignService;
import io.dao.gulimall.ware.service.WareOrderTaskDetailService;
import io.dao.gulimall.ware.service.WareOrderTaskService;
import io.dao.gulimall.ware.service.WareSkuService;
import io.dao.gulimall.ware.vo.OrderVo;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@RabbitListener(queues = {"stock.release.stock.queue"})
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockedTo to, Message message, Channel channel) throws IOException {
        System.out.println("收到库存解锁消息");
        try {
            wareSkuService.handleStockLockedRelease(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

    @RabbitHandler
    public void handleOrderCloseRelease(OrderTo to, Message message, Channel channel) throws IOException {
        System.out.println("订单关闭准备解锁消息");
        try {
            wareSkuService.handleOrderCloseRelease(to);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(), false);
        } catch (Exception e) {
            channel.basicReject(message.getMessageProperties().getDeliveryTag(), true);
        }
    }

}
