package io.dao.gulimall.order.web;

import com.alipay.api.AlipayApiException;
import io.dao.gulimall.order.config.AlipayTemplate;
import io.dao.gulimall.order.service.OrderService;
import io.dao.gulimall.order.vo.PayVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PayWebController {

    @Autowired
    private AlipayTemplate alipayTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * 将支付页让游览器展示
     * 支付成功后，我们要跳到用户的订单列表页
     */
    @ResponseBody
    @GetMapping(value = "/payOrder", produces = "text/html")
    public String payOrder(@RequestParam("orderSn") String orderSn) throws AlipayApiException {
        PayVo payVo = orderService.getOrderPay(orderSn);
        // 返回的是一个页面
        String pay = alipayTemplate.pay(payVo);
        return pay;
    }

}
