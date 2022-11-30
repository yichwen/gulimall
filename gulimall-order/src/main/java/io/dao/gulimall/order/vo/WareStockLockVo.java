package io.dao.gulimall.order.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareStockLockVo {
    private String orderSn;
    private List<OrderItemVo> locks;    // 需要锁住的库存信息
}
