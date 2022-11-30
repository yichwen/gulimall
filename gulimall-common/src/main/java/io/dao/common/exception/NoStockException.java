package io.dao.common.exception;

import lombok.Getter;

public class NoStockException extends RuntimeException {

    @Getter
    private Long skuId;

    public NoStockException(Long skuId) {
        super("商品id:" + skuId + ":没有足够的库存");
    }

    public NoStockException(String msg) {
        super(msg);
    }

}
