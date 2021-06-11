package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.entity.ExtractEntity;
import com.cf.crs.mapper.ExtractMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


/**
 * 分层
 */
@Slf4j
@Service
public class ExtractService extends ServiceImpl<ExtractMapper, ExtractEntity> implements IService<ExtractEntity> {

    /**
     * 获取平台抽层
     * @return
     */
    public ExtractEntity getExtractEntityByType(Integer type){
        return baseMapper.selectOne(new QueryWrapper<ExtractEntity>().eq("t_project_type",type).last(" limit 1"));
    }


}
