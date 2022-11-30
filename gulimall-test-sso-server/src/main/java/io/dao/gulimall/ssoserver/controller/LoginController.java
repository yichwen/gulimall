package io.dao.gulimall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Controller
public class LoginController {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/userinfo")
    public String userInfo(@RequestParam String token) {
        return (String) redisTemplate.opsForValue().get(token);
    }

    @GetMapping("/login.html")
    public String loginPage(@RequestParam(value = "redirect_uri", required = false) String url,
                            Model model,
                            @CookieValue(value = "sso_token", required = false) String token) {
        // 读取Cookie，判断是否有用户已登录
        if (StringUtils.hasText(token)) {
            if (url != null) {
                return "redirect:" + url + "?token=" + token;
            } else {
                // TODO: 已登录，但是没有重定向URL，暂时转会登录页面
                return "redirect:http://ssoserver.com:8080/login.html";
            }
        }
        // 重定向的URL（从其他客户端跳转过来的URL），如果有则设置，没有就不需要
        if (url != null) {
            // 为了能够在登录页面进行登录时能获取此URL
            model.addAttribute("url", url);
        }
        return "login";
    }

    @PostMapping("/doLogin")
    public String doLogin(String username, String password, String url, HttpServletResponse response) {
        if (StringUtils.hasText(username) && StringUtils.hasText(password)) {
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid, username);
            // 设置Cookie，记录有用户已登录
            response.addCookie(new Cookie("sso_token", uuid));
            if (StringUtils.hasText(url)) {
                return "redirect:" + url + "?token=" + uuid;
            } else {
                // TODO: 已登录，但是没有重定向URL，暂时转会登录页面
                return "redirect:http://ssoserver.com:8080/login.html";
            }
        }
        if (StringUtils.hasText(url)) {
            return "redirect:http://ssoserver.com:8080/login.html?redirect_uri=" + url;
        } else {
            return "redirect:http://ssoserver.com:8080/login.html";
        }
    }


}
