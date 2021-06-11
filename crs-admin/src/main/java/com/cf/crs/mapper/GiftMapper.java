package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.EmailSenderProperties;
import com.cf.crs.entity.GiftEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface GiftMapper extends BaseDao<GiftEntity> {
}
