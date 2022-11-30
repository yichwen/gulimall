package io.dao.gulimall.cart.controller;

import io.dao.gulimall.cart.interceptor.CartInterceptor;
import io.dao.gulimall.cart.service.CartService;
import io.dao.gulimall.cart.vo.Cart;
import io.dao.gulimall.cart.vo.CartItem;
import io.dao.gulimall.cart.vo.UserInfoTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/currentUserCartItems")
    public List<CartItem> getCurrentUserCartItems() {
        return cartService.getCurrentUserCartItems();
    }


    @GetMapping("/deleteItem")
    public String deleteItemBySkuId(Long skuId) {
        cartService.deleteItem(skuId);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/countItem")
    public String changeItemCount(Long skuId, Integer count) {
        cartService.changeItemCount(skuId, count);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    @GetMapping("/checkItem")
    public String checkItem(Long skuId, Integer check) {
        cartService.checkItem(skuId, check);
        return "redirect:http://cart.gulimall.com/cart.html";
    }

    /**
     * 游览器有一个cookie: user-key标识用户身份，一个月后过期
     * 如果第一次使用购物车功能，都会给一个临时用户身份
     *
     * 已登录，session就会有用户数据
     * 没登录，按照 cookie 里面带来 user-key 来判断
     *  - 如果没有 user-key，就需要创建一个新的
     */
    @GetMapping("cart.html")
    public String cartListPage(Model model) {
        Cart cart = cartService.getCart();
        model.addAttribute("cart", cart);
        return "cartList";
    }

    /**
     * redirectAttributes:
     *  #addFlashAttribute() - 将数据放在session里面可以在页面取出，但是只能取一次
     *  #addAttribute() - 将数据放在url后面
     * 添加
     */
    @GetMapping("/addToCart")
    public String addToCart(Long skuId, Integer num, RedirectAttributes redirectAttributes) {
        cartService.addToCart(skuId, num);
        redirectAttributes.addAttribute("skuId", skuId);
        return "redirect:http://cart.gulimall.com/addToCartSuccess.html";
    }

    @GetMapping("/addToCartSuccess.html")
    public String addToCartSuccessPage(Long skuId, Model model) {
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("item", cartItem);
        return "success";
    }

}
