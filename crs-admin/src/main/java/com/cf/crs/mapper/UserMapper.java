package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.ExtractEntity;
import com.cf.crs.entity.UserEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface UserMapper extends BaseDao<UserEntity> {
}
