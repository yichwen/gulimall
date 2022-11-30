package io.dao.gulimall.order.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderSubmitVo {
    private Long addrId;        // 收货地址 id
    private Integer payType;    // 支付方式
    // 无需提交要购买的商品，去购物车再获取一遍
    // 问：这样处理真的好吗？

    // 优惠，发票

    private String orderToken;  // 防重令牌
    private BigDecimal payPrice;   // 为了验价功能

    private String note; // 订单备注

    // 用户相关信息将从 session 获取
}
