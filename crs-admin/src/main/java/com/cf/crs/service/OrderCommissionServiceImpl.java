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
     */
    @Transactional
    public int saveOrderCommission() {
        return saveOrderCommission(0);
    }

    @Transactional
    public int saveOrderCommission(Integer day) {
        day=day==null?0:day;
        int total = 0;
        long  end= DateUtils.getBeforeZeroHourSecondMils(System.currentTimeMillis(), day);
        long start = DateUtils.getBeforeZeroHourSecondMils(end,  -1);

        List<Map<String, Object>> list = orderService.getTotalPaymentBetweenTime(start, end);
        if (CollectionUtils.isEmpty(list)) {
            log.info("saveOrderCommission->[{},{}] size->0", start, end );
            return 0;
        } else log.info("saveOrderCommission->[{},{}] size->{}", start, end, list.size());
        for (Map<String, Object> map : list) {
            total += saveEntity(map,start) ? 1 : 0;
        }
        return total;
    }

    private boolean saveEntity(Map<String, Object> map,long time) {
        String roomId = map.get("room_id").toString();
        double total = Double.parseDouble(map.get("total").toString());
        OrderCommissionEntity orderCommissionEntity = OrderCommissionEntity.builder()
                .ctime(time) .roomId(roomId).status(0)
                .profit(orderLeverService.getOrderCommission().get("commission").getLever() * total).build();

        OrderCommissionEntity orderC= this.getBaseMapper().selectOne(
                new QueryWrapper<OrderCommissionEntity>()
                        .eq("ctime", orderCommissionEntity.getCtime())
                        .eq("room_id", roomId));
        //如果没有记录则插入
        if(orderC==null)
        {
            return this.save(orderCommissionEntity);
        }
        else
        {
            //如果状态已经更新则不操作
            if(orderC.getStatus()>0) return false;
            else
            { //如果状态还是未处理，则更新
                orderCommissionEntity.setId(orderC.getId());
                orderCommissionEntity.setCtime(orderC.getCtime());
                return this.updateById(orderCommissionEntity);
            }
        }
    }


}
