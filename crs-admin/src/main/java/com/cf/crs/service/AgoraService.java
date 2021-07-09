package com.cf.crs.service;


import com.cf.crs.properties.AgoraConfigProperties;
import com.cf.crs.properties.AgoraParam;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.Const;
import com.cf.util.utils.DataChange;
import com.cf.util.utils.agora.RtcTokenBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;


/**
 * 声网token
 */
@Slf4j
@Service
public class AgoraService {

    @Autowired
    AgoraConfigProperties agoraConfigProperties;

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    //appid：c600202c72484e6d8f29e6c053500734
    // app证书：3fccaa040ea9432c91e59ab9c9208672


    public static void main(String[] args) throws Exception {

        String appId = "970CA35de60c44645bbae8a215061b33";
        String appCertificate = "5CFd2fd1755d40ecb72977518be15d3b";
        String channelName = "7d72365eb983485397e3e3f9d460bdda";
        String userAccount = "2082341273";
        int uid = 2082341273;
        int expirationTimeInSeconds = 3600;

        RtcTokenBuilder token = new RtcTokenBuilder();
        int timestamp = (int)(System.currentTimeMillis() / 1000 + expirationTimeInSeconds);
        String result = token.buildTokenWithUserAccount(appId, appCertificate,
                channelName, userAccount, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);

        result = token.buildTokenWithUid(appId, appCertificate,
                channelName, uid, RtcTokenBuilder.Role.Role_Publisher, timestamp);
        System.out.println(result);
    }

    /**
     * 获取声网token
     * @param agoraParam
     * @return
     */
    public ResultJson<String> getAgoraToken(AgoraParam agoraParam){
        if (agoraParam.getUid() == null || StringUtils.isEmpty(agoraParam.getChannelName())) return HttpWebResult.getMonoError("param error");
        try {
            //先查看redis缓存
            String token = Const.AGORA_TOKEN + agoraParam.getUid()+"_"+agoraParam.getChannelName();
            String result = redisTemplate.opsForValue().get(token);
            if (StringUtils.isNotEmpty(result)) return HttpWebResult.getMonoSucResult(result);
            //缓存中不存在，声网接口获取token并做缓存
            RtcTokenBuilder rtcTokenBuilder = new RtcTokenBuilder();
            int timestamp = (int)(System.currentTimeMillis() / 1000 + agoraConfigProperties.getExpirationTimeInSeconds());
            result = rtcTokenBuilder.buildTokenWithUid(agoraConfigProperties.getAppId(), agoraConfigProperties.getAppCertificate(),
                    agoraParam.getChannelName(), DataChange.obToInt(agoraParam.getUid()), RtcTokenBuilder.Role.Role_Publisher, timestamp);
            log.info("agore token->{},{},{}",agoraParam.getUid(),agoraParam.getChannelName(),result);
            redisTemplate.opsForValue().set(token,result,agoraConfigProperties.getCacheTimeInSeconds(), TimeUnit.SECONDS);
            return HttpWebResult.getMonoSucResult(result);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
            return HttpWebResult.getMonoError(e.getMessage());
        }
    }

}
