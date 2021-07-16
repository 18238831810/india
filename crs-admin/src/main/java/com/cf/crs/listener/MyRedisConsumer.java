package com.cf.crs.listener;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cf.crs.service.CashinRebateService;
import com.cf.util.utils.Const;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MyRedisConsumer implements StreamListener<String, ObjectRecord<String, String>> {

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    CashinRebateService cashinRebateService;

    @Override
    @SneakyThrows
    public void onMessage(ObjectRecord<String, String> record) {
        String message = record.getValue();
        try {
            log.info("监听到消息[{}]", message);
            JSONObject jsonObject = JSON.parseObject(message, JSONObject.class);
            String tag = jsonObject.getString("tag");
            if (Const.CASHIN_TAG.equalsIgnoreCase(tag)){
                //注册充值奖励
                cashinRebateService.saveCashinRebate(jsonObject.getLong("message"));
            }
        } catch (Exception e) {
           log.error(e.getMessage(),e);
        } finally {
            stringRedisTemplate.opsForStream().acknowledge(Const.REDIS_STREAM_TOPIC, record);
        }
    }

}
