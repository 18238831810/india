package com.cf.crs.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.entity.GiftEntity;
import com.cf.crs.mapper.GiftMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 赠送礼物
 */
@Slf4j
@Service
public class GiftService extends ServiceImpl<GiftMapper, GiftEntity> implements IService<GiftEntity> {

    /**
     * 根据id获取礼物信息
     * @return
     */
    public GiftEntity getGiftEntitybyId(Integer giftId){
        return this.getById(giftId);
    }


}
