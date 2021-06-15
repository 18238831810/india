package com.cf.crs.config.config;

import com.cf.crs.common.utils.Result;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.MessageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * @author frank
 * @description 全局响应处理器
 * @date 2019/10/15 9:59
 */
@ControllerAdvice
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        return true;
    }

    @Nullable
    @Override
    public Object beforeBodyWrite(@Nullable Object o,MethodParameter methodParameter, MediaType mediaType,Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest serverHttpRequest,
                                  ServerHttpResponse serverHttpResponse) {

       if (o instanceof ResultJson) {
            //message国际化封装方式(只封装message)
           ResultJson<Object> response = (ResultJson<Object>) o;
           String message = response.getMsg();
           if (StringUtils.isNotEmpty(message) && message.startsWith("msg_")){
               response.setMsg(MessageUtil.get(message));
           }
           return response;
       }else if (o instanceof Result){
           //message国际化封装方式(只封装message)
           Result<Object> response = (Result<Object>) o;
           String message = response.getMsg();
           if (StringUtils.isNotEmpty(message) && message.startsWith("msg_")){
               response.setMsg(MessageUtil.get(message));
           }
       }
        return o;

    }

}
