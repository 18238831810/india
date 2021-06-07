package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.FinancialDetailsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资金明细
 * @author frank
 * @date 2021-06-05
 **/
@Mapper
public interface FinancialDetailsMapper extends BaseDao<FinancialDetailsEntity> {
}
