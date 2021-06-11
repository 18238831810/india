package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.PlatformIncomeEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface PlatformIncomeMapper extends BaseDao<PlatformIncomeEntity> {

    /**
     * 平台收益更新
     * @param platformIncomeEntity
     * @return
     */
    Integer updatePlatfoamIncome(@Param("item") PlatformIncomeEntity platformIncomeEntity);



}
