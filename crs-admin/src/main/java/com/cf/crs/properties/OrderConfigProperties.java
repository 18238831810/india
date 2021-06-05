package com.cf.crs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 下单配置
 * @author frank
 * @date 20210-06-05
 *
 **/
@Data
@Component
@ConfigurationProperties(prefix = "order.config")
@EnableConfigurationProperties(OrderConfigProperties.class)
public class OrderConfigProperties implements Serializable {

    /**
     * 商户id
     */
    private Integer merchId;

    /**
     * api_token
     */
    private String apiToken;

    /**
     * 代收款下单地址
     */
    private String orderUrl;

    /**
     * 代收款下单回调地址
     */
    private String orderCallbackUrl;

    /**
     * 代付款下单地址
     */
    private String collectionUrl;

    /**
     * 代付款下单回调地址
     */
    private String collectionCallbackUrl;

}
