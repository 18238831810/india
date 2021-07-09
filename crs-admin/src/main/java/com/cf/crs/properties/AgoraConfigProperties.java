package com.cf.crs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author frank
 * @description 声网接入配置
 * @date 2019/8/13 16:06
 */
@Data
@Component
@ConfigurationProperties(prefix = "agora.config")
@EnableConfigurationProperties(AgoraConfigProperties.class)
public class AgoraConfigProperties implements Serializable {


    /**
     * appId
     */
    private String appId;

    /**
     * 证书
     */
    private String appCertificate;

    /**
     * token过期的 Unix 时间戳
     */
    private Integer expirationTimeInSeconds;

    /**
     * 缓存过期时间
     */
    private Integer cacheTimeInSeconds;

}
