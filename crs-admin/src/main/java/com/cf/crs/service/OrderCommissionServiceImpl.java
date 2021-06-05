package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cf.crs.common.utils.DateUtils;
import com.cf.crs.entity.OrderCommissionEntity;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.mapper.OrderCommissionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


/**
 * 直播返利统计处理
 */
@Service
@Slf4j
public class OrderCommissionServiceImpl extends ServiceImpl<OrderCommissionMapper, OrderCommissionEntity> implements IService<OrderCommissionEntity> {

    @Autowired
    OrderLeverServiceImpl orderLeverService;

    @Autowired
    OrderServiceImpl orderService;

    /**
     * 保存直播室的抽成返利记录
     *
     */
    @Transactional
    public void saveOrderCommission() {
        List<Map<String, Object>> list = orderService.getTotalPaymentYestoday();
        if (CollectionUtils.isEmpty(list)) {
            log.warn("没有数据要统计");
        }
        for (Map<String, Object> map : list) {
            saveEntity(map);
        }

    }

    private void saveEntity(Map<String, Object> map) {
        String roomId = map.get("room_id").toString();
        double total = Double.parseDouble(map.get("total").toString());

        OrderCommissionEntity orderCommissionEntity = OrderCommissionEntity.builder()
                .ctime(DateUtils.getBeforeZeroHourSecondMils(System.currentTimeMillis(), -1))
                .roomId(roomId)
                .profit(orderLeverService.getOrderCommission().get("commission").getLever() * total).build();
        Integer count = this.getBaseMapper().selectCount(new QueryWrapper<OrderCommissionEntity>().eq("ctime", orderCommissionEntity.getCtime()).eq("room_id", roomId));
        if (count == null || count <= 0)
            this.save(orderCommissionEntity);
    }


}
