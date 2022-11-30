package io.dao.gulimall.order.vo;

import lombok.Data;

@Data
public class SkuHasStockVo {
    private Long skuId;
    private boolean hasStock;
}
