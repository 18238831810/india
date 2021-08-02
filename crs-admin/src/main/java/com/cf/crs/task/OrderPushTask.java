package com.cf.crs.task;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.cf.crs.entity.SettingOrderEntity;
import com.cf.crs.job.task.ITask;
import com.cf.crs.mapper.SettingOrderMapper;
import com.cf.crs.service.OrderServiceImpl;
import com.cf.crs.service.SendRedisMessage;
import com.cf.util.utils.Const;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 订单处理
 */
@Slf4j
@Component("orderPushTask")
public class OrderPushTask implements ITask {

    @Autowired
    SettingOrderMapper settingOrderMapper;

    @Autowired
    SendRedisMessage sendRedisMessage;


    @Override
    public void run(String params) {
        try {
            List<SettingOrderEntity> list = settingOrderMapper.selectList(new QueryWrapper<SettingOrderEntity>());
            if (CollectionUtils.isNotEmpty(list)){
                sendRedisMessage.send(JSON.toJSONString(list), Const.ORDER_SETTING_TAG);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }



}
