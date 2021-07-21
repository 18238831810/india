package com.cf.crs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * 腾讯sdk
 * @author frank
 * @date 20210-07-20
 *
 **/
@Data
@Component
@ConfigurationProperties(prefix = "tcsdk.config")
@EnableConfigurationProperties(TXSdkProperties.class)
public class TXSdkProperties implements Serializable {

    /**
     * sdkappid
     */
    private Long sdkappid;

    /**
     * identifier
     */
    private String identifier;

    /**
     * 密匙
     */
    private String key;

    /**
     * 域名
     */
    private String serveName;

    /**
     *  群发通知地址
     */
    private String systemNotificationUrl;



}
