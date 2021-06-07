package com.cf.crs.config.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.cf.crs.common.exception.AuthException;
import com.cf.util.utils.Const;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.asm.Advice;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author leek
 * @description 拦截器
 * @date 2019/8/13 15:06
 */
@Configuration
@Slf4j
public class ActionInterceptor implements HandlerInterceptor {

    private final static String  TOKEN_TEMP="20210607_binanace";
    @Autowired
    RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String url = request.getServletPath();
        log.info("url:{},params:{}",url,JSONObject.toJSONString(request.getParameterMap()));
        long startTime = System.currentTimeMillis();
        request.setAttribute("request_per_handleTime", startTime);
        boolean isAuth = authCondition(url,request);
        if (isAuth) return true;
        throw new AuthException();
    }

    /**
     * 判断权限
     * @param url
     * @param request
     * @return
     */
    private boolean authCondition(String url,HttpServletRequest request) {
        if (url.startsWith(Const.PUBLIC)) {
            return true;
        }else if (url.startsWith(Const.API)){
            return checkLoin(request);
        }else if(url.startsWith(Const.ADMIN)){
            return checkLoinForAdmin(request);
        }
        return true;
    }

    /**
     * 检测后台用户权限
     * @param request
     * @return
     */
    private boolean checkLoinForAdmin(HttpServletRequest request)
    {
        return true;
    }

    /**
     * 检测用户是否登录
     * @param request
     * @return
     */
    private boolean checkLoin(HttpServletRequest request)
    {
        String token =request.getHeader("token");
        String uid =request.getHeader("t_id");
        String loginedKey ="token_" + uid;
        if(TOKEN_TEMP.equalsIgnoreCase(token)&& StringUtils.isNotBlank(uid))
        {
            return true;
        }
        if(!redisTemplate.hasKey(loginedKey) || StringUtils.isBlank(uid))
        {
            log.info("uid->{} token->{} not logined",uid,token);
            return false;
        }
        Object ob =redisTemplate.boundHashOps("token_" + uid).get("t_token");
        if(ob==null)
        {
            log.info("uid->{} token->{} not cahce not exist",uid,token);
            return false;
        }
        return true;
    }
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (request.getServletPath().equals("/error")) {
            //不打印
        } else {
            long startTime = (Long) request.getAttribute("request_per_handleTime");
            request.removeAttribute("request_per_handleTime");
            long endTime = System.currentTimeMillis();
            log.info("url:{},time:{}",request.getServletPath(),endTime - startTime);
        }
    }

}
