package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binance.api.client.constant.OrderErrorEnum;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.entity.OrderLeverEntity;
import com.cf.crs.mapper.OrderLeverMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class OrderLeverServiceImpl extends ServiceImpl<OrderLeverMapper, OrderLeverEntity> implements IService<OrderLeverEntity> {

    private final String  ORDER_RANGE_KEY="u:order:range:key";

    @Autowired
    RedisTemplate redisTemplate;
    /**
     * 买方向对应的返利倍数
     * @return
     */
    public Map<String, OrderLeverEntity> getBuyDirectLever() {
        return getOrderLeverMap(0);
    }

    /**
     * 下单的最小最大值
     * @return
     */
    public Map<String, OrderLeverEntity> getOrderRange() {
        return getOrderLeverMap(1);
    }

    public Map<String, OrderLeverEntity> getOrderLeverMap(int type) {
        Map<String, OrderLeverEntity> map = new HashMap<>();
        List<OrderLeverEntity> list = this.getBaseMapper().selectList(new QueryWrapper<OrderLeverEntity>().eq("type", type));
        for (OrderLeverEntity orderLeverEntity : list) {
            map.put(orderLeverEntity.getName(), orderLeverEntity);
        }
        return map;
    }

    /**
     * 获取下单的最大最小值
     * @return
     */
    public double[] getCacheOrderRange()
    {
        BoundValueOperations<String,String> boundValueOperations = redisTemplate.boundValueOps(ORDER_RANGE_KEY);
        String minAndMax =boundValueOperations.get();
        if(StringUtils.isEmpty(minAndMax))
        {
            Map<String, OrderLeverEntity> orderRange= getOrderRange();
            if(orderRange==null) return null;
            double max =(orderRange.get("max")!=null && orderRange.get("max").getLever()>0)?orderRange.get("max").getLever():Double.MAX_VALUE;
            double min=(orderRange.get("min")!=null && orderRange.get("min").getLever()>0)?orderRange.get("min").getLever():0.1;
            boundValueOperations.set(min+"_"+max);
            return new double[]{min,max};
        }
        else
        {
            return  Arrays.asList(minAndMax.split("_")).stream().mapToDouble(Double::parseDouble).toArray();
        }
    }


    /**
     * 下单金额是否在区间范围内
     * @param orderEntity
     * @return
     */
    public OrderErrorEnum betweenMinAndMax(OrderEntity orderEntity)
    {
        double[] minMax= getCacheOrderRange();
        boolean ok= orderEntity.getPayment()>=minMax[0] && orderEntity.getPayment()<=minMax[1];
        if(!ok)
        {
            log.info("token->{} 下单的值->{}不在区间内[{},{}]",orderEntity.getToken(), orderEntity.getPayment(),minMax[0],minMax[1]);
            OrderErrorEnum orderErrorEnum =OrderErrorEnum.ERROR_NOT_BETWEEN;
            orderErrorEnum.setError(String.format(OrderErrorEnum.ERROR_NOT_BETWEEN.getError(),minMax[0],minMax[1]));
            return orderErrorEnum;
        }
        return null;
    }


}
