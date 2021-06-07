package com.cf.crs.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    @Autowired
    RedisTemplate redisTemplate;

    public long getUid()
    {
        long uid =Long.parseLong(getTokenFromHeader("t_id"));
        return uid;
    }

    public String  getTokenFromHeader(String key)
    {
        return fetchCurrentRequest().getHeader(key);
    }

    /**
     * 返回当前的request
     *
     * @return
     */
    public HttpServletRequest fetchCurrentRequest() {
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return sra.getRequest();
    }
}
