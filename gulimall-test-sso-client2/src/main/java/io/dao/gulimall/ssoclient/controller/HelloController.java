package io.dao.gulimall.ssoclient.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Controller
public class HelloController {

    @Value("${sso.server.url}")
    private String ssoServerUrl;

    // 无需登录
    @ResponseBody
    @GetMapping("/hello")
    public String hello() {
        return "hello";
    }

    @GetMapping("/boss")
    public String employees(Model model, HttpSession session, @RequestParam(required = false) String token) {

        if (StringUtils.hasText(token)) {
            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> responseEntity = restTemplate.getForEntity("http://ssoserver.com:8080/userinfo?token=" + token, String.class);
            String userInfo = responseEntity.getBody();
            session.setAttribute("loginUser", userInfo);
        }

        Object loginUser = session.getAttribute("loginUser");
        if (loginUser == null) {
            // 没登录，跳转到登录页面
            System.out.println(ssoServerUrl);
            return "redirect:" + ssoServerUrl + "?redirect_uri=http://client2.com:8082/boss";
        }
        List<String> emps = new ArrayList<>();
        emps.add("zhangsan");
        emps.add("lisi");
        model.addAttribute("emps", emps);
        return "list";
    }

}
