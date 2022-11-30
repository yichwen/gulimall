package io.dao.gulimall.order.listener;

import com.alipay.api.internal.util.AlipaySignature;
import io.dao.gulimall.order.config.AlipayTemplate;
import io.dao.gulimall.order.service.OrderService;
import io.dao.gulimall.order.vo.PayAsyncVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
public class OrderPaidListener {

    @Autowired
    private OrderService orderService;

    @Autowired
    private AlipayTemplate alipayTemplate;

    /**
     * 必须是外网可访问的 设置alipay.notify-url
     * 需要映射 nginx 如果使用网络穿透
     * server_name 网络穿透域名;
     * location /paid/ {
     *     proxy_set_header Host order.gulimall.com;
     *     proxy_pass http://gulimall.com
     * }
     */
    @PostMapping("/paid/notification")
    public String handleAlipayNotification(PayAsyncVo vo, HttpServletRequest request) {
        // 可以查看支付宝发送的数据
        Map<String, String[]> map = request.getParameterMap();
        // 验证签名
//        boolean signVerified = AlipaySignature.rsaCheckV1()

        // 如果签名验证成功才更新订单状态
        String result = orderService.handleAlipayNotification(vo);
        // 返回"success"通知支付接受成功，支付宝就不会再发送消息
        return result;
        // 如果签名验证失败，返回 "error"
    }
}
