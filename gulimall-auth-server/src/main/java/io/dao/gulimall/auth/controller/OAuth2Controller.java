package io.dao.gulimall.auth.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.dao.common.utils.HttpUtils;
import io.dao.common.utils.R;
import io.dao.common.vo.MemberRespVo;
import io.dao.gulimall.auth.feign.MemberFeignService;
import io.dao.gulimall.auth.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static io.dao.common.constant.AuthServerConstant.LOGIN_USER;

@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;

    @GetMapping("/oauth2/weibo/success")
    public String weibo(@RequestParam String code, HttpSession httpSession) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "accessKey");
        map.put("client_secret", "accessSecret");
        map.put("grant_type", "code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2/weibo/success");
        map.put("code", code);
        HttpResponse response = HttpUtils.doPost("api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, "");
        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            // 当前用户如果是第一次进网站，自动注册进来（当前社交用户生成一个会员信息账号）
            // 登录或则注册社交用户
            R r = memberFeignService.oauth2Login(socialUser);
            if (r.getCode() == 0) {
                MemberRespVo data = r.getData(new TypeReference<MemberRespVo>(){});
                httpSession.setAttribute(LOGIN_USER, data);
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }
    }

}
