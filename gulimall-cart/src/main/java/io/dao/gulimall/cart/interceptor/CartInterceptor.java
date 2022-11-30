package io.dao.gulimall.cart.interceptor;

import io.dao.common.constant.CartConstant;
import io.dao.common.vo.MemberRespVo;
import io.dao.gulimall.cart.vo.UserInfoTo;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.UUID;

import static io.dao.common.constant.AuthServerConstant.LOGIN_USER;
import static io.dao.common.constant.CartConstant.TEMP_USER_COOKIE_NAME;
import static io.dao.common.constant.CartConstant.TEMP_USER_COOKIE_TIMEOUT;

/**
 * 在执行目标方法之前，判断用户的登陆状态
 * 并封装传递给 controller 目标请求
 */
public class CartInterceptor implements HandlerInterceptor {

    public static ThreadLocal<UserInfoTo> threadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        UserInfoTo userInfo = new UserInfoTo();
        HttpSession session = request.getSession();
        MemberRespVo loginUser = (MemberRespVo) session.getAttribute(LOGIN_USER);
        if (loginUser != null) {
            userInfo.setUserId(loginUser.getId());
        }

        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {
            for (Cookie cookie : cookies) {
                String name = cookie.getName();
                if (TEMP_USER_COOKIE_NAME.equals(name)) {
                    userInfo.setUserKey(cookie.getValue());
                    userInfo.setTempUser(true);
                }
            }
        }

        // 如果没有临时用户一定要分配一个临时用户
        if (StringUtils.isEmpty(userInfo.getUserKey())) {
            String uuid = UUID.randomUUID().toString();
            userInfo.setUserKey(uuid);
        }
        threadLocal.set(userInfo);
        return true;
    }

    /**
     * 业务执行之后，分配临时用户，让游览器保存
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserInfoTo userInfoTo = threadLocal.get();
        // 也可以不判断是不是现有临时用户，这样会持续延长临时用户过期时间
        if (!userInfoTo.isTempUser()) {
            Cookie cookie = new Cookie(TEMP_USER_COOKIE_NAME, userInfoTo.getUserKey());
            cookie.setDomain("gulimall.com");
            cookie.setMaxAge(TEMP_USER_COOKIE_TIMEOUT);
            response.addCookie(cookie);
        }
    }

}
