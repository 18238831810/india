package com.cf.crs.filter;

import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author ：frank
 * @description： 请求参数处理器
 * @date ：Created in 2020/6/10 17:15
 */
@Component
public class ParamsFilter implements Filter{

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new ParamsRequestWrapper((HttpServletRequest) req), res);
    }

}

    