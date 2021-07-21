package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.properties.TXSdkProperties;
import com.cf.util.utils.CacheKey;
import com.cf.util.utils.TLSSigAPIv2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 腾讯sdk
 */
@Slf4j
@Service
public class TXSdkService {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    @Autowired
    TXSdkProperties TXSdkProperties;

    @Autowired
    RestTemplate restTemplate;

    /*{
        "GroupId": "@TGS#2C5SZEAEF",
        "From_Account": "leckie", // 指定消息发送者（选填）
        "Random": 8912345, // 随机数字，五分钟数字相同认为是重复消息
        "MsgBody": [ // 消息体，由一个element数组组成，详见字段说明
            {
                "MsgType": "TIMTextElem", // 文本
                "MsgContent": {
                    "Text": "red packet"
                }
            },
        ]
    }*/

    public void sendOrderMessage(OrderEntity orderEntity){
        Map<String, Object> map = new HashMap<String, Object>(2);
        String content = new StringBuffer("用户").append(orderEntity.getUid()).append("在").append("分时交易玩法中,买入").append(orderEntity.getPayment()).toString();
        JSONObject text = new JSONObject();
        text.put("type","order");
        text.put("content",content);
        text.put("button","跟买");
        //text.put("uid", orderEntity.getUid());
        text.put("buyDirection",orderEntity.getBuyDirection());

        getMessageBody(orderEntity, map, text,1);
    }

    public void sendOrderProfitMessage(OrderEntity orderEntity){
        if(orderEntity.getStatus() != 1 || orderEntity.getProfit() <= 0) return;
        Map<String, Object> map = new HashMap<String, Object>(2);
        String content = new StringBuffer("您获得").append(orderEntity.getProfit()).append("奖励").toString();
        JSONObject text = new JSONObject();
        text.put("type","profit");
        text.put("content",content);
        text.put("button","查看详情");
        text.put("uid", orderEntity.getUid());

        getMessageBody(orderEntity, map, text,2);
    }

    /**
     * 封装消息体
     * @param orderEntity
     * @param map
     * @param text
     * @param type 1:下单 2:盈利
     */
    private void getMessageBody(OrderEntity orderEntity, Map<String, Object> map, JSONObject text,Integer type) {
        JSONObject msgContent = new JSONObject();
        msgContent.put("Text",text.toJSONString());

        JSONObject msgBodyJson = new JSONObject();
        //文本
        msgBodyJson.put("MsgType","TIMTextElem");
        msgBodyJson.put("MsgContent",msgContent);

        JSONArray msgBody = new JSONArray();
        msgBody.add(msgBodyJson);

        map.put("GroupId","10423");
        map.put("Random",""+type+orderEntity.getId());
        map.put("MsgBody",msgBody);
        log.info(JSON.toJSONString(map));
        sendMessage(map);
    }


    public JSONObject sendMessage(Map<String, Object> map){
        String identifier = TXSdkProperties.getIdentifier();
        String key = TXSdkProperties.getKey();
        Long sdkappid = TXSdkProperties.getSdkappid();
        String userSig = getUserSig(identifier, key, sdkappid);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map> request = new HttpEntity(map, httpHeaders);
        String url = new StringBuffer(TXSdkProperties.getServeName()).append(TXSdkProperties.getSystemNotificationUrl()).append("?").append("usersig=").
                append(userSig).append("&identifier=").append(identifier).append("&sdkappid=").append(sdkappid).append("&random=99999999&contenttype=json").toString();
        log.info("send msg :{}",url);
        JSONObject result = restTemplate.postForObject(url, request, JSONObject.class);
        log.info("tc send result:{}",result.toJSONString());
        return result;
    }

    /**
     * 获取userSig
     * @param identifier
     * @param key
     * @param sdkappid
     * @return
     */
    private String getUserSig(String identifier, String key, Long sdkappid) {
        String result = redisTemplate.opsForValue().get(CacheKey.TCSDK_USERSIG);
        if (StringUtils.isNotEmpty(result)) return result;
        TLSSigAPIv2 tlsSigAPIv2 = new TLSSigAPIv2(sdkappid, key);
        String userSig = tlsSigAPIv2.genUserSig(identifier, 3600);
        try {
            if(StringUtils.isNotEmpty(userSig)) redisTemplate.opsForValue().set(CacheKey.TCSDK_USERSIG,userSig,3500, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        }
        return userSig;
    }
}
