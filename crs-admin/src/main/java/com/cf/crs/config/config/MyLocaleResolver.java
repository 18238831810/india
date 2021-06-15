package com.cf.crs.config.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

public class MyLocaleResolver implements LocaleResolver {

    @Override
    public Locale resolveLocale(HttpServletRequest request) {
        String newLocale = request.getParameter("lang");
        if (StringUtils.isEmpty(newLocale)) newLocale = request.getHeader("lang");
        if (StringUtils.isEmpty(newLocale)) newLocale = "en_IN";
        return org.springframework.util.StringUtils.parseLocaleString(newLocale);
    }

    @Override
    public void setLocale(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Locale locale) {

    }
}
