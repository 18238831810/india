package com.cf.crs.config.interceptor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.common.exception.AuthException;
import com.cf.util.utils.Const;
import com.cf.util.utils.DataChange;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        String token =request.getHeader(Const.TOKEN);
        String uid =request.getHeader(Const.UID);
        log.info("token:{},uid:{}",token,uid);
        //此行只做测试使用
        if (TOKEN_TEMP.equalsIgnoreCase(token)) return true;
        if(StringUtils.isEmpty(token) || StringUtils.isBlank(uid)) return false;
        //获取用户缓存信息
        Map<String,Object> userVerify = redisTemplate.opsForHash().entries("token_" + uid);
        log.info("userVerify:{}", JSON.toJSONString(userVerify));
        //改用用户存在缓存信息，并且请求的token匹配，则放行
        if (userVerify != null  && !userVerify.isEmpty()) {
            if (token.equals(DataChange.obToString(userVerify.get("t_token")))) {
                redisTemplate.expire("token_" + uid,2, TimeUnit.HOURS);
                return true;
            }
        }
        return false;
       /* //真实使用时  ，uid应该从token中获取，不能让从参数取，防止有人恶意盗取或者使用别人的余额
        String token =request.getHeader(Const.TOKEN);
        String uid =request.getHeader(Const.UID);
        log.info("token:{},uid:{}",token,uid);
        String loginedKey ="token_" + uid;
        if(TOKEN_TEMP.equalsIgnoreCase(token))
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
        //此处逻辑，token中获取uid，覆盖传参中的uid，前端或者app去除请求头中或者传参中的uid

        return true;*/
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
