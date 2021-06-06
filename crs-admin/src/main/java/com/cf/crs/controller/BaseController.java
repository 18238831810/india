package com.cf.crs.controller;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

public abstract class BaseController {

    public long getUid()
    {
        String token =getTokenFromHeader();
        return 0l;
    }

    public String  getTokenFromHeader()
    {
        return fetchCurrentRequest().getHeader("token");
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
