package com.cf.crs.config.config;

import com.cf.crs.config.interceptor.ActionInterceptor;
import com.cf.util.utils.Const;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

/**
 * @author leek
 * @description：拦截器加载配置
 * @date 2019/8/13 15:06
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurationSupport {

    @Bean
    public ActionInterceptor getActionInterceptor() {
        return new ActionInterceptor();
    }


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        //放行swagger资源
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");


        registry.addResourceHandler("/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");

        //放行modeler.html
        registry.addResourceHandler("modeler.html").addResourceLocations("classpath:/public/");
        super.addResourceHandlers(registry);
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        /*调用创建的InterceptorConfig
         * addPathPatterns("/sys/**)都要进入InterceptorConfig拦截
         * excludePathPatterns("/login")不用进入到InterceptorConfig拦截
         * 因为token效验已在过滤器处理此处我们不做处理
         */
        registry.addInterceptor(getActionInterceptor()).addPathPatterns(Const.ADMIN_URL,Const.API_URL,Const.PUBLIC_URL)
                /*放行swagger*/
                .excludePathPatterns("/swagger-resources/**", "/v2/**", "/swagger-ui.html/**", "/modeler.html/**");

        super.addInterceptors(registry);
    }

}
