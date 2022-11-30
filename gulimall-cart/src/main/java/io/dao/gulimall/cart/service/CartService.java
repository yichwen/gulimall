package io.dao.gulimall.cart.service;

import io.dao.gulimall.cart.vo.Cart;
import io.dao.gulimall.cart.vo.CartItem;

import java.util.List;

public interface CartService {
    CartItem addToCart(Long skuId, Integer num);
    CartItem getCartItem(Long skuId);
    Cart getCart();
    void clearCart(String cartKey);
    // 修改购物商品打勾状态
    void checkItem(Long skuId, Integer check);
    // 修改购物商品数量
    void changeItemCount(Long skuId, Integer count);
    // 删除购物项
    void deleteItem(Long skuId);
    // 获取登录用户购物车列表
    List<CartItem> getCurrentUserCartItems();
}
