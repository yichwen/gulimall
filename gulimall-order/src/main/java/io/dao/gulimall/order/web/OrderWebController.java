package io.dao.gulimall.order.web;

import io.dao.common.exception.NoStockException;
import io.dao.gulimall.order.service.OrderService;
import io.dao.gulimall.order.vo.OrderConfirmVo;
import io.dao.gulimall.order.vo.OrderSubmitVo;
import io.dao.gulimall.order.vo.SubmitOrderResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class OrderWebController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/toTrade")
    public String toTrade(Model model) {
        OrderConfirmVo confirmVo = orderService.confirmOrder();
        model.addAttribute("orderConfirmData", confirmVo);
        return "confirm";
    }

    /**
     * 提交订单
     */
    @PostMapping("/submitOrder")
    public String submitOrder(OrderSubmitVo vo, Model model, RedirectAttributes redirectAttributes) {
        // 下单：去创建订单，检验令牌，检验价格，锁库存
        try {
            SubmitOrderResponseVo response = orderService.submitOrder(vo);
            if (response.getCode() == 0) {
                // 成功后转到支付选项页
                model.addAttribute("order", response.getOrder());
                return "pay";
            } else {
                String msg = "下单失败：";
                switch (response.getCode()) {
                    case 1:
                        msg += "订单信息过期，请刷新再次提交";
                        break;
                    case 2:
                        msg += "订单商品价格发生变化，请确认后再次提交";
                        break;
//                case 3: msg += "库存锁定失败，商品库存不足"; break;
                }
                // 失败后转回确认订单页
                redirectAttributes.addFlashAttribute("msg", msg);
                return "redirect:http://order.gulimall.com/toTrade";
            }
        } catch (Exception e) {
            if (e instanceof NoStockException) {
                String msg = "下单失败，商品无库存";
                redirectAttributes.addFlashAttribute("msg", msg);
            }
            return "redirect:http://order.gulimall.com/toTrade";
        }
    }

}
