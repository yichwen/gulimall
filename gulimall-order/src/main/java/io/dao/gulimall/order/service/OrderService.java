package io.dao.gulimall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.dao.common.utils.PageUtils;
import io.dao.gulimall.order.entity.OrderEntity;
import io.dao.gulimall.order.vo.*;

import java.util.Map;

/**
 * @author yichwenlim
 * @email yichwenlim@gmail.com
 * @date 2020-10-09 16:11:25
 */
public interface OrderService extends IService<OrderEntity> {
    PageUtils queryPage(Map<String, Object> params);
    OrderConfirmVo confirmOrder();
    SubmitOrderResponseVo submitOrder(OrderSubmitVo vo);
    OrderEntity getOrderByOrderSn(String orderSn);
    void closeOrder(OrderEntity order);
    PayVo getOrderPay(String orderSn);
    String handleAlipayNotification(PayAsyncVo vo);
}

