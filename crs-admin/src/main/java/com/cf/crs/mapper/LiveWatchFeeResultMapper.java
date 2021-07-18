package com.cf.crs.mapper;

import com.cf.crs.common.dao.BaseDao;
import com.cf.crs.entity.LiveWatchFeeEntity;
import com.cf.crs.entity.LiveWatchFeeResultEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author frank
 * 2019/10/16
 **/
@Mapper
public interface LiveWatchFeeResultMapper extends BaseDao<LiveWatchFeeResultEntity> {

    int updateOrInsert(@Param("item")LiveWatchFeeEntity liveWatchFeeEntity);

}
