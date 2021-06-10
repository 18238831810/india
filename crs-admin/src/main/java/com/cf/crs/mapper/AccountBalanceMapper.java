package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.AccountBalanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountBalanceMapper extends BaseDao<AccountBalanceEntity> {

    /**
     * 用户余额加钱
     * @param accountBalanceEntity
     * @return
     */
    Integer updateAddBalance(@Param("item") AccountBalanceEntity accountBalanceEntity);

    /**
     * 用户余额扣钱
     * @param accountBalanceEntity
     * @return
     */
    Integer updateSubBalance(@Param("item") AccountBalanceEntity accountBalanceEntity);

}
