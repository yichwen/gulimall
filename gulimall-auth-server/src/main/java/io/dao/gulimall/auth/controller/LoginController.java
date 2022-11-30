package io.dao.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import io.dao.common.constant.AuthServerConstant;
import io.dao.common.exception.BizCodeEnum;
import io.dao.common.utils.R;
import io.dao.common.vo.MemberRespVo;
import io.dao.gulimall.auth.feign.MemberFeignService;
import io.dao.gulimall.auth.feign.ThirdpartyFeignService;
import io.dao.gulimall.auth.vo.UserLoginVo;
import io.dao.gulimall.auth.vo.UserRegistVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static io.dao.common.constant.AuthServerConstant.LOGIN_USER;
import static io.dao.common.constant.AuthServerConstant.SMS_CODE_CACHE_PREFIX;

@Controller
public class LoginController {

    /**
     * 发送一个请求直接跳转到一个页面
     * SpringMVC view controller：将请求和页面映射
     */
//    @GetMapping("/login.html")
//    public String loginPage() {
//        return "login";
//    }
//
//    @GetMapping("/reg.html")
//    public String regPage() {
//        return "reg";
//    }

    @Autowired
    private ThirdpartyFeignService thirdpartyFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @ResponseBody
    @GetMapping("/sms/sendcode")
    public R sendCode(@RequestParam String phone) {
        // 接口防刷
        String redisCode = redisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + phone);
        if (StringUtils.hasText(redisCode)) {
            long l = Long.parseLong(redisCode.split("_")[1]);
            if (System.currentTimeMillis() - l < 60000) {
                // 60秒内不能再发
                return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMsg());
            }
        }
        // 验证码校验
        String code = UUID.randomUUID().toString().substring(0, 5);
        String value = code + "_" + System.currentTimeMillis();
        System.out.println("code=" + value);
        redisTemplate.opsForValue().set(SMS_CODE_CACHE_PREFIX + phone, value, 10, TimeUnit.MINUTES);
//        thirdpartyFeignService.sendCode(phone, code);
        return R.ok();
    }

    /**
     * TODO: 重定向携带数据，利用session原理，将数据放在session中，只要跳到下一个页面取出数据后，session里面的数据就会删除掉
     * TODO: 分布式session问题
     * redirectAttributes 模拟重定向携带数据
     */
    @PostMapping("/register")
    public String register(@Valid UserRegistVo vo, BindingResult result, Model model, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
//            Map<String, String> errors = result.getFieldErrors().stream()
//                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            Map<String, String> errors = new HashMap<>();
            for (FieldError fieldError : result.getFieldErrors()) {
                errors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage());
            }
            redirectAttributes.addFlashAttribute("errors", errors);

            // 如果直接渲染页面，不好因为刷新会重新触发注册请求，因为游览器URL已改为 /register
            // model.addAttribute("errors", errors);
            // return "reg";

            // 如果使用转发，转发会继续使用 POST 方法请求
            // return "forward:/reg.html";

            // 重定向
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        // 校验验证码
        String code = vo.getCode();
        String redisCode = redisTemplate.opsForValue().get(SMS_CODE_CACHE_PREFIX + vo.getPhone());
        if (StringUtils.hasText(redisCode)) {
            if (code.equals(redisCode.split("_")[0])) {
                // 删除验证码
                redisTemplate.delete(SMS_CODE_CACHE_PREFIX + vo.getPhone());
                // 验证码通过，远程调用服务进行注册
                R r = memberFeignService.register(vo);
                if (r.getCode() != 0) {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
                    redirectAttributes.addFlashAttribute("errors", errors);
                    // 重定向
                    return "redirect:http://auth.gulimall.com/reg.html";
                }

            } else {
                Map<String, String> errors = new HashMap<>();
                errors.put("code", "验证码错误");
                redirectAttributes.addFlashAttribute("errors", errors);
                // 重定向
                return "redirect:http://auth.gulimall.com/reg.html";
            }
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("code", "验证码错误");
            redirectAttributes.addFlashAttribute("errors", errors);
            // 重定向
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        // 注册成功回到登录页
        return "redirect:http://auth.gulimall.com/login.html";
    }

    @GetMapping("/login.html")
    public String loginPage(HttpSession httpSession) {
        Object attribute = httpSession.getAttribute(LOGIN_USER);
        if (attribute != null) {
            // 已登录，跳转到首页
            return "redirect:http://gulimall.com";
        }
        return "login";
    }

    @PostMapping("/login")
    public String login(UserLoginVo vo, RedirectAttributes redirectAttributes, HttpSession httpSession) {
        R r = memberFeignService.login(vo);
        if (r.getCode() != 0) {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", r.getData("msg", new TypeReference<String>(){}));
            redirectAttributes.addFlashAttribute("errors", errors);
            // 重定向
            return "redirect:http://auth.gulimall.com/login.html";
        }
        MemberRespVo data = r.getData(new TypeReference<MemberRespVo>(){});
        httpSession.setAttribute(LOGIN_USER, data);
        return "redirect:http://gulimall.com";
    }

}
