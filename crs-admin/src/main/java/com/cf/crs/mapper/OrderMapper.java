package com.cf.crs.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cf.crs.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper  extends BaseMapper<OrderEntity> {
}
