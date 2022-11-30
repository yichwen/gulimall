package io.dao.gulimall.product.vo;

import lombok.Data;

@Data
public class SkuHasStockVo {
    private Long skuId;
    private boolean hasStock;
}
