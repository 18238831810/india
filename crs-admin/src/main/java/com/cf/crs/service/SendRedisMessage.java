package com.cf.crs.service;


import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.util.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * redis消息队列监听器
 */
@Slf4j
@Service
public class SendRedisMessage {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void send(String message,String tag) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tag",tag);
            jsonObject.put("message",message);
            ObjectRecord<String, String> record = StreamRecords.objectBacked(JSON.toJSONString(jsonObject)).withStreamKey(Const.REDIS_STREAM_TOPIC);
            RecordId recordId = stringRedisTemplate.opsForStream().add(record);
            log.info("消息发送成功{}", recordId);
        } catch (Exception e) {
            throw new RuntimeException(StrUtil.format("消息发送失败[{}]", message));
        }
    }

}
