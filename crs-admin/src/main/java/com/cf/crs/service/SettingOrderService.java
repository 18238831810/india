package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.entity.QueryPage;
import com.cf.crs.entity.SettingOrderEntity;
import com.cf.crs.mapper.SettingOrderMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 自定义下单数据
 */
@Slf4j
@Service
public class SettingOrderService extends ServiceImpl<SettingOrderMapper, SettingOrderEntity> implements IService<SettingOrderEntity> {

    /**
     * 查询用户的充值列表
     *
     * @param dto
     * @return
     */
    public PagingBase<SettingOrderEntity> queryList(QueryPage dto) {
        Page<SettingOrderEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        IPage<SettingOrderEntity> pageList = this.page(iPage, new QueryWrapper<SettingOrderEntity>());
        List<SettingOrderEntity> records = pageList.getRecords();
        return new PagingBase<SettingOrderEntity>(records, pageList.getTotal());
    }

    /**
     * 新增或者更改数据
     *
     * @param entity
     * @return
     */
    public ResultJson<String> updateEntity(SettingOrderEntity entity) {
        if (entity.getId() != null){
            baseMapper.updateById(entity);
        }else {
            baseMapper.insert(entity);
        }
        return HttpWebResult.getMonoSucStr();
    }


    public ResultJson<String> deleteEntity(Long id) {
        if (id != null){
            baseMapper.deleteById(id);
        }
        return HttpWebResult.getMonoSucStr();
    }





}
