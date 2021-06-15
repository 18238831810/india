package com.cf.crs.config.config;


import com.cf.util.utils.MessageUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.context.MessageSource;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author frank
 * @date 2020/5/7 13:49
 * @description 国际化语言listener
 */
@Component
public class ApplicationEvent implements ApplicationListener<ContextRefreshedEvent> {

    @Resource
    protected MessageSource messageSource;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        MessageUtil.setMessageSource(messageSource);
    }
}
