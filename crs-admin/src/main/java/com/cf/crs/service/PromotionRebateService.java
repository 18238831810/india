package com.cf.crs.service;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.entity.PromotionRebateEntity;
import com.cf.crs.mapper.PromotionRebateMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.CacheKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 推广返利规则设置
 */
@Slf4j
@Service
public class PromotionRebateService extends ServiceImpl<PromotionRebateMapper, PromotionRebateEntity> implements IService<PromotionRebateEntity> {

    @Autowired
    RedisTemplate<String,String> redisTemplate;

    /**
     * 获取推广返利规则设置（后台展示使用）
     *
     * @return
     */
    public ResultJson<PromotionRebateEntity> queryList() {
        List<PromotionRebateEntity> list = baseMapper.selectList(new QueryWrapper<PromotionRebateEntity>());
        return HttpWebResult.getMonoSucResult(list);
    }

    /**
     * 修改推广返利规则设置
     *
     * @return
     */
    public ResultJson<String> updateSetting(PromotionRebateEntity promotionRebateEntity) {
        promotionRebateEntity.setUpdateTime(System.currentTimeMillis());
        baseMapper.updateById(promotionRebateEntity);
        cacheSetting();
        return HttpWebResult.getMonoSucStr();
    }

    private PromotionRebateEntity cacheSetting(){
        PromotionRebateEntity promotionRebateEntity = baseMapper.selectById(1);
        if (promotionRebateEntity != null) {
            redisTemplate.opsForValue().set(CacheKey.PROMOTION_REBATE_SETTING, JSON.toJSONString(promotionRebateEntity));
        }
        return promotionRebateEntity;
    }

    /**
     * 获取推广返利规则设置（内部统计使用）
     * @return
     */
    public PromotionRebateEntity getPromotionRebateSetting(){
        String redis = redisTemplate.opsForValue().get(CacheKey.PROMOTION_REBATE_SETTING);
        if (StringUtils.isNotEmpty(redis)) return JSON.parseObject(redis,PromotionRebateEntity.class);
        return cacheSetting();
    }

}
