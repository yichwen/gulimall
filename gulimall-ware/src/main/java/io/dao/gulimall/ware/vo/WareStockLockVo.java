package io.dao.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

@Data
public class WareStockLockVo {
    private String orderSn;
    private List<OrderItemVo> locks;    // 需要锁住的库存信息

}
