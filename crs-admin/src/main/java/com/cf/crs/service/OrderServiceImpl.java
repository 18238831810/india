package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.constant.OrderErrorEnum;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.cf.crs.common.utils.DateUtils;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.entity.OrderLeverEntity;
import com.cf.crs.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;


/**
 * 订单处理类
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderEntity> implements IService<OrderEntity> {

    @Autowired
    OrderLeverServiceImpl orderLeverService;


    @Autowired
    AccountBalanceService accountBalanceService;
    /**
     * 下单的三个方向常量
     */
    private final static List<String> buyDirect = Arrays.asList("rise", "fall", "equal");
    /**
     * 多久之内可以下单
     */
    private final static long LIMIT_TIME = 40;

    @Transactional
    public OrderErrorEnum saveUserOrder(OrderEntity orderEntity) {

        OrderErrorEnum orderErrorEnum = filterParam(orderEntity);
        if (orderErrorEnum != null)
            return orderErrorEnum;

        orderEntity.setEarlyStageTime(getTimeKey(orderEntity.getCtime()));
        return saveOrder(orderEntity);
    }

    private OrderErrorEnum filterParam(OrderEntity orderEntity) {
        int second = LocalDateTime.now().getSecond();
        if (second >= LIMIT_TIME) {
            log.warn("已经过时了 second->{},token->{}", second, orderEntity.getToken());
            return OrderErrorEnum.ERROR_OVER_TIME;
        }
        orderEntity.setCtime(System.currentTimeMillis());

        if (StringUtils.isEmpty(orderEntity.getBuyDirection())
                || (!buyDirect.contains(orderEntity.getBuyDirection()))) {
            log.warn("direct->{} token->{}", orderEntity.getBuyDirection(), orderEntity.getToken());
            return OrderErrorEnum.ERROR_PARAM;
        }

        OrderErrorEnum orderErrorEnum = orderLeverService.betweenMinAndMax(orderEntity);
        if (orderErrorEnum != null) {
            return orderErrorEnum;
        }

        return null;
    }

    /**
     * roomId token orderPrice
     */
    public OrderErrorEnum saveOrder(OrderEntity orderEntity) {

        //判断余额是否足够
        AccountBalanceEntity accountBalanceEntity= accountBalanceService.getAccountBalanceByUId(orderEntity.getUid());
        if(accountBalanceEntity==null || accountBalanceEntity.getAmount()<orderEntity.getPayment())
        {
            log.info("uid->{} payment->{} not enough",orderEntity.getUid(),orderEntity.getPayment());
            return OrderErrorEnum.ERROR_NOT_ENOUGH;
        }

        orderEntity.setNextStageTime(orderEntity.getEarlyStageTime() + 60000);

        orderEntity.setUid(getUidFromToken(orderEntity.getToken()));

        orderEntity.setMarketCycle(CandlestickInterval.ONE_MINUTE.getIntervalId());
        this.save(orderEntity);
        //TODO
        //从用户账户扣钱
        accountBalanceEntity.setAmount((float)-orderEntity.getPayment());
        accountBalanceEntity.setUpdateTime(System.currentTimeMillis());
        accountBalanceService.updateBalance(accountBalanceEntity);
        return null;
    }

    private long getUidFromToken(String token) {
        return 0l;
    }

    /**
     * 以分钟为最小单位去获取时间
     *
     * @param time
     * @return
     */
    private long getTimeKey(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    private String getPointPrize(String number) {
        DecimalFormat df = new DecimalFormat("#.00");
        return df.format(BigDecimal.valueOf(Double.valueOf(number)));
    }

    /**
     * 根据实时获取当时的行情
     *
     * @param timeKey
     * @return
     */
    private Candlestick getCandlestick(Long timeKey) {
       return  CandlesticksCache.getInstance().getCandlesticksCache().get(timeKey);
//        Candlestick candlestick =
//        if (candlestick == null) {
//            CandlesticksCache candlesticksCache = CandlesticksCache.getInstance();
//            candlesticksCache.cache();
//            return candlesticksCache.getCandlesticksCache().get(timeKey);
//        } else return candlestick;
    }


    @Transactional
    public int updateOrder() {

        Map<String, OrderLeverEntity> orderLeverEntityMap = orderLeverService.getBuyDirectLever();

        long end = DateUtils.getZeroSecondMils(System.currentTimeMillis());
        long  start= end - 60 * 60000;
        List<OrderEntity> list = this.getBaseMapper().selectList(
                new QueryWrapper<OrderEntity>()
                        .le("utime", 0)
                        .ge("ctime", start)
                        .lt("ctime", end)
                        .last(" limit 50"));
        int total=0;
        for (OrderEntity orderEntity : list) {
            OrderLeverEntity orderLeverEntity = orderLeverEntityMap.get(orderEntity.getBuyDirection());
            if (orderLeverEntity == null) {
                log.warn("ID->{} 对应的方向->{} 没有找到", orderEntity.getId(), orderEntity.getBuyDirection());
                continue;
            }
            total+= updateOrder(orderEntity, orderLeverEntity);
        }
        return total;
    }


    public int updateOrder(OrderEntity orderEntity, OrderLeverEntity orderLeverEntity) {
       return updateOrder(orderEntity, orderLeverEntity.getLever());
    }

    public int updateOrder(OrderEntity orderEntity, double lever) {
         Candlestick earlyStage = getCandlestick(orderEntity.getEarlyStageTime());
        if (earlyStage == null) {
            log.warn("获取earlyStage行情时获取不到->{} id->{}", orderEntity.getEarlyStageTime(), orderEntity.getId());
            return 0;
        }
        orderEntity.setEarlyStagePrice(getPointPrize(earlyStage.getOpen()));
        orderEntity.setEarlyStageTime(earlyStage.getOpenTime());

        Candlestick nextStage = getCandlestick(orderEntity.getNextStageTime());
        if (nextStage == null) {
            log.warn("获取nextStage行情时获取不到->{},id->{}", orderEntity.getNextStageTime(), orderEntity.getId());
            return 0;
        }
        orderEntity.setNextStagePrice(getPointPrize(nextStage.getOpen()));
        orderEntity.setUtime(System.currentTimeMillis());
        orderEntity.setLever(lever);
        setTotalProfit(orderEntity, lever);
        return this.updateById(orderEntity)?1:0;
    }

    private void setTotalProfit(OrderEntity orderEntity, double lever) {
        if (!isWinLotteryResults(orderEntity)) {
            orderEntity.setProfit(-orderEntity.getPayment());
        } else {
            orderEntity.setProfit(orderEntity.getPayment() * lever);
        }
    }

    /**
     * 开奖结果对比
     *
     * @param orderEntity
     * @return
     */
    private boolean isWinLotteryResults(OrderEntity orderEntity) {
        switch (orderEntity.getBuyDirection()) {
            case "rise": {
                if (Double.valueOf(orderEntity.getNextStagePrice()) > Double.valueOf((orderEntity.getEarlyStagePrice())))
                    return true;
                return false;
            }
            case "fall": {
                if (Double.valueOf(orderEntity.getNextStagePrice()) < Double.valueOf((orderEntity.getEarlyStagePrice())))
                    return true;
                return false;
            }
            case "equal": {
                if (Double.valueOf(orderEntity.getNextStagePrice()) == Double.valueOf((orderEntity.getEarlyStagePrice())))
                    return true;
                return false;
            }
            default:
                return false;
        }
    }

    public List<Map<String, Object>> getTotalPaymentYestoday() {
        long now = System.currentTimeMillis();
        long start = DateUtils.getBeforeZeroHourSecondMils(now, -1);
        long end = DateUtils.getBeforeZeroHourSecondMils(now, 0);
        return getTotalPaymentBetweenTime(start, end);
    }

    public List<Map<String, Object>> getTotalPaymentBetweenTime(long start, long end) {
        return this.getBaseMapper().selectMaps(new QueryWrapper<OrderEntity>().ge("ctime", start).lt("ctime", end).select("sum(payment) as total", "room_id").groupBy("room_id"));
    }

}
