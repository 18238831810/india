package com.cf.crs.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.entity.PlatformIncomeEntity;
import com.cf.crs.entity.ProfitDetailEntity;
import com.cf.crs.mapper.PlatformIncomeMapper;
import com.cf.crs.mapper.ProfitDetailMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 收益详情列表
 */
@Slf4j
@Service
public class PlatformIncomeService extends ServiceImpl<PlatformIncomeMapper, PlatformIncomeEntity> implements IService<PlatformIncomeEntity> {


    public Integer updatePlatfoamIncome(PlatformIncomeEntity platformIncomeEntity){
        return baseMapper.updatePlatfoamIncome(platformIncomeEntity);
    }

}
