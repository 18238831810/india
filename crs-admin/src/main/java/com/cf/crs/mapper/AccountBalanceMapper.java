package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.AccountBalanceEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface AccountBalanceMapper extends BaseDao<AccountBalanceEntity> {

    Integer updateBalance(@Param("item") AccountBalanceEntity accountBalanceEntity);

}
