package io.dao.gulimall.member.web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MemberWebController {

    @GetMapping("/memberOrder.html")
    public String memberOrderPage(@RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum, Model model) {
        // 获取支付宝给我们传来的所有请求数据
        // request: 验证签名，如果正确可以去修改

        // 查出当前登录用户的所有订单列表数据
        // 渲染在 第306课 暂时跳过
        // https://www.youtube.com/watch?v=lmNYtG1Y240&list=PLmOn9nNkQxJEwPjhNwGliP_bw3RjkgFCf&index=307

//        Map<String, Object> page = new HashMap<>();
//        page.put("page", pageNum.toString());
//        R r = orderFeignService.listWithItem(page);
//        model.addAttribute("orders", r);
        return "orderList";
    }

}
