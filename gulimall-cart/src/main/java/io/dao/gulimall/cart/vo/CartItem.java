package io.dao.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 购物项内容
 */
@Data
public class CartItem {
    private Long skuId;
    private Boolean check = true;
    private String title;
    private String image;
    private List<String> skuAttr;
    private BigDecimal price;
    private Integer count;
//    private BigDecimal totalPrice;

    public BigDecimal getTotalPrice() {
         return this.price.multiply(new BigDecimal(this.count));
    }

}
