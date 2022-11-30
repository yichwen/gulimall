package io.dao.gulimall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class Cart {
    private List<CartItem> items;
//    private Integer countNum;   // 商品数量
//    private Integer countType;  // 商品类型数量
//    private BigDecimal totalAmount;
    private BigDecimal reduce = BigDecimal.ZERO;  // 减免价格

    public Integer getCountNum() {
        int count = 0;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                count += item.getCount();
            }
        }
        return count;
    }

    public Integer getCountType() {
        return items != null ? items.size() : 0;
    }

    public BigDecimal getTotalAmount() {
        BigDecimal amount = BigDecimal.ONE;
        if (items != null && items.size() > 0) {
            for (CartItem item : items) {
                if (item.getCheck()) {
                    amount = amount.add(item.getTotalPrice());
                }
            }
        }
        return amount.subtract(this.reduce);
    }

}
