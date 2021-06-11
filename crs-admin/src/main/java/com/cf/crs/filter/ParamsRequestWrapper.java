package com.cf.crs.filter;

import com.cf.util.utils.Const;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;

/**
 * @author ：frank
 * @description： 请求参数处理器
 * @date ：Created in 2020/6/10 16:57
 */
public class ParamsRequestWrapper extends HttpServletRequestWrapper {

    public ParamsRequestWrapper(HttpServletRequest request){
        super(request);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Enumeration<String> enumeration = super.getParameterNames();
        ArrayList<String> list = Collections.list(enumeration);
        String servletPath = getServletPath();
        if (servletPath.startsWith(Const.API)) addBusinessType(list);
        return Collections.enumeration(list);
    }

    /**
     * 自动填充uid到请求参数中
     * @param list
     */
    private void addBusinessType(ArrayList<String> list) {
        if (StringUtils.isNotEmpty(getHeader(Const.UID)))  list.add(Const.UID);
    }

    @Override
    public String[] getParameterValues(String name) {
        if (Const.UID.equals(name)){
            return new String[]{getHeader(Const.UID)};
        }
        return super.getParameterValues(name);
    }



}

    