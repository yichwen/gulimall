package io.dao.gulimall.cart.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.dao.common.utils.R;
import io.dao.gulimall.cart.feign.ProductFeignService;
import io.dao.gulimall.cart.interceptor.CartInterceptor;
import io.dao.gulimall.cart.service.CartService;
import io.dao.gulimall.cart.vo.Cart;
import io.dao.gulimall.cart.vo.CartItem;
import io.dao.gulimall.cart.vo.SkuInfoVo;
import io.dao.gulimall.cart.vo.UserInfoTo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CartServiceImpl implements CartService {

    private final String CART_PREFIX = "gulimall:cart:";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private ThreadPoolExecutor executor;

    @Override
    public CartItem addToCart(Long skuId, Integer num) {

        BoundHashOperations<String, Object, Object> cartOps = getCartOps();

        String cartJson = (String) cartOps.get(skuId.toString());
        if (StringUtils.isEmpty(cartJson)) {
            // 添加新商品
            CartItem cartItem = new CartItem();
            cartItem.setCheck(true);
            cartItem.setCount(num);
            cartItem.setSkuId(skuId);

            // 远程查询当前要添加的商品的信息
            CompletableFuture<Void> skuInfoFuture = CompletableFuture.runAsync(() -> {
                R r = productFeignService.getSkuInfo(skuId);
                if (r.getCode() == 0) {
                    SkuInfoVo skuInfo = r.getData("skuInfo", new TypeReference<SkuInfoVo>() {
                    });
                    cartItem.setImage(skuInfo.getSkuDefaultImg());
                    cartItem.setPrice(skuInfo.getPrice());
                }
            }, executor);

            // 远程查询当前要添加的商品的信息
            CompletableFuture<Void> skuAttrFuture = CompletableFuture.runAsync(() -> {
                List<String> skuSaleAttrValue = productFeignService.getSkuSaleAttrValue(skuId);
                cartItem.setSkuAttr(skuSaleAttrValue);
            }, executor);

            try {
                CompletableFuture.allOf(skuInfoFuture, skuAttrFuture).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem ;
        } else {
            CartItem cartItem = JSON.parseObject(cartJson, CartItem.class);
            cartItem.setCount(num + cartItem.getCount());
            String s = JSON.toJSONString(cartItem);
            cartOps.put(skuId.toString(), s);
            return cartItem;
        }
    }

    @Override
    public CartItem getCartItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        String cartJson = (String) cartOps.get(skuId.toString());
        return JSON.parseObject(cartJson, CartItem.class);
    }

    @Override
    public Cart getCart() {

        Cart cart = new Cart();

        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        if (userInfoTo.getUserId() != null) {
            // 登录
            // 如果临时购物车的数据还没有合并
            String tempCartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> tempCartItems = getCartItems(tempCartKey);
            if (tempCartItems != null && tempCartItems.size() > 0) {
                // 临时购物车有数据，需要合并
                for (CartItem item : tempCartItems) {
                    addToCart(item.getSkuId(), item.getCount());
                }
                // 清除临时购物车的数据
                clearCart(tempCartKey);
            }
            // 在获取登录用户的购物车【包含合并的购物车数据】
            String cartKey = CART_PREFIX + userInfoTo.getUserId();
            List<CartItem> cartItems = getCartItems(cartKey);
            if (cartItems != null) {
                cart.setItems(cartItems);
            }
            return cart;

        } else {
            // 没登录
            String cartKey = CART_PREFIX + userInfoTo.getUserKey();
            List<CartItem> cartItems = getCartItems(cartKey);
            if (cartItems != null) {
                cart.setItems(cartItems);
            }
        }
        return cart;
    }

    /**
     * 获取购物车的操作
     */
    private BoundHashOperations<String, Object, Object> getCartOps() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            // e.g. gulimall:cart:6
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            // e.g. gulimall:cart:<uuid>
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }

    private List<CartItem> getCartItems(String cartKey) {
        BoundHashOperations<String, Object, Object> cartOps = redisTemplate.boundHashOps(cartKey);
        List<Object> values = cartOps.values();
        if (values != null && values.size() > 0) {
            List<CartItem> items = values.stream().map((obj) -> {
                String json = (String) obj;
                CartItem cartItem = JSON.parseObject(json, CartItem.class);
                return cartItem;
            }).collect(Collectors.toList());
            return items;
        }
        return null;
    }

    @Override
    public void clearCart(String cartKey) {
        redisTemplate.delete(cartKey);
    }

    @Override
    public void checkItem(Long skuId, Integer check) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCheck(check == 1);
        String json = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), json);
    }

    @Override
    public void changeItemCount(Long skuId, Integer count) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        CartItem cartItem = getCartItem(skuId);
        cartItem.setCount(count);
        String json = JSON.toJSONString(cartItem);
        cartOps.put(skuId.toString(), json);
    }

    @Override
    public void deleteItem(Long skuId) {
        BoundHashOperations<String, Object, Object> cartOps = getCartOps();
        cartOps.delete(skuId.toString());
    }

    @Override
    public List<CartItem> getCurrentUserCartItems() {
        UserInfoTo userInfoTo = CartInterceptor.threadLocal.get();
        String cartKey = "";
        if (userInfoTo.getUserId() != null) {
            // e.g. gulimall:cart:6
            cartKey = CART_PREFIX + userInfoTo.getUserId();
        } else {
            // e.g. gulimall:cart:<uuid>
            cartKey = CART_PREFIX + userInfoTo.getUserKey();
        }
        List<CartItem> checkedCartItems = getCartItems(cartKey).stream()
                .filter(CartItem::getCheck)
                .map(item -> {
                    BigDecimal price = productFeignService.getPrice(item.getSkuId());
                    // 更新价格
                    item.setPrice(price);
                    return item;
                })
                .collect(Collectors.toList());
        return checkedCartItems;
    }

}