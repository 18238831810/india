package com.cf.crs.service;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.entity.FinancialDetailsDto;
import com.cf.crs.entity.FinancialDetailsEntity;
import com.cf.crs.mapper.FinancialDetailsMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 资金明细
 * @author frank
 * @date 2021-06-07
 */
@Slf4j
@Service
public class FinancialDetailsService extends ServiceImpl<FinancialDetailsMapper, FinancialDetailsEntity> implements IService<FinancialDetailsEntity> {


    /**
     * 批量保存用户资金明细记录
     * @param list
     */
    public void myInsertBatch(List<FinancialDetailsEntity> list){
        baseMapper.myInsertBatch(list);
    }

    /**
     * 查询用户的资金明细列表
     *
     * @param dto
     * @return
     */
    public PagingBase<FinancialDetailsEntity> list(FinancialDetailsDto dto) {
        Page<FinancialDetailsEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        IPage<FinancialDetailsEntity> pageList = this.page(iPage, new QueryWrapper<FinancialDetailsEntity>().eq("uid", dto.getUid())
        .eq(dto.getType() != null,"type",dto.getType()).ge(dto.getStartTime() != null,"order_time",dto.getStartTime())
        .le(dto.getEndTime() != null,"order_time",dto.getEndTime()).orderByDesc("order_time"));
        return new PagingBase<FinancialDetailsEntity>(pageList.getRecords(), pageList.getTotal());
    }



}
