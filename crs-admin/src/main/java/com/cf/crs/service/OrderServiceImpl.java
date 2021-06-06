package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.constant.OrderErrorEnum;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.entity.QueryPage;
import com.cf.crs.common.utils.DateUtils;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.CandlestickDto;
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
            log.warn("已经过时了 second->{},id->{}", second, orderEntity.getId());
            return OrderErrorEnum.ERROR_OVER_TIME;
        }
        orderEntity.setCtime(System.currentTimeMillis());

        if (StringUtils.isEmpty(orderEntity.getBuyDirection())
                || (!buyDirect.contains(orderEntity.getBuyDirection()))) {
            log.warn("direct->{} id->{}", orderEntity.getBuyDirection(), orderEntity.getId());
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
        AccountBalanceEntity accountBalanceEntity = accountBalanceService.getAccountBalanceByUId(orderEntity.getUid());
        if (accountBalanceEntity == null || accountBalanceEntity.getAmount() < orderEntity.getPayment()) {
            log.info("uid->{} payment->{} not enough", orderEntity.getUid(), orderEntity.getPayment());
            return OrderErrorEnum.ERROR_NOT_ENOUGH;
        }

        orderEntity.setNextStageTime(orderEntity.getEarlyStageTime() + 60000);

        orderEntity.setMarketCycle(CandlestickInterval.ONE_MINUTE.getIntervalId());
        this.save(orderEntity);
        //TODO
        //从用户账户扣钱
        accountBalanceEntity.setAmount((float) -orderEntity.getPayment());
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
        return CandlesticksCache.getInstance().getCandlesticksCache().get(timeKey);
    }


    @Transactional
    public int updateOrder() {

        Map<String, OrderLeverEntity> orderLeverEntityMap = orderLeverService.getBuyDirectLever();

        long end = DateUtils.getZeroSecondMils(System.currentTimeMillis());
        long start = end - 60 * 60000;
        List<OrderEntity> list = this.getBaseMapper().selectList(
                new QueryWrapper<OrderEntity>()
                        .le("utime", 0)
                        .ge("ctime", start)
                        .lt("ctime", end)
                        .eq("status", 0)
                        .last(" limit 50"));
        int total = 0;
        for (OrderEntity orderEntity : list) {
            OrderLeverEntity orderLeverEntity = orderLeverEntityMap.get(orderEntity.getBuyDirection());
            if (orderLeverEntity == null) {
                log.warn("ID->{} 对应的方向->{} 没有找到", orderEntity.getId(), orderEntity.getBuyDirection());
                continue;
            }
            total += updateOrder(orderEntity, orderLeverEntity);
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
        orderEntity.setStatus(1);
        setTotalProfit(orderEntity, lever);
        updateBalance(orderEntity);
        return this.updateById(orderEntity) ? 1 : 0;
    }

    private void updateBalance(OrderEntity orderEntity) {
        //如果是盈利了，则给总账户加钱
        if (orderEntity.getProfit() > 0) {
            AccountBalanceEntity accountBalanceEntity = accountBalanceService.getAccountBalanceByUId(orderEntity.getUid());
            accountBalanceEntity.setAmount((float) (orderEntity.getProfit() + orderEntity.getPayment()));
            accountBalanceEntity.setUpdateTime(System.currentTimeMillis());
            log.info("uid->{} profit->{}", orderEntity.getUid(), orderEntity.getProfit());
            accountBalanceService.updateBalance(accountBalanceEntity);
        }
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

    /**
     * 统计指定时间内的返利
     *
     * @param start
     * @param end
     * @return
     */
    public List<Map<String, Object>> getTotalPaymentBetweenTime(long start, long end) {
        return this.getBaseMapper().selectMaps(
                new QueryWrapper<OrderEntity>()
                        .ge("ctime", start)
                        .lt("ctime", end)
                        .eq("status", 1)
                        .select("sum(payment) as total", "room_id")
                        .groupBy("room_id"));
    }

    /**
     * 更新订单状态为删除状态
     *
     * @param orderEntity
     * @return
     */
    public OrderErrorEnum updateOrderToDelStatus(OrderEntity orderEntity) {
        OrderEntity order = this.getBaseMapper().selectOne(new QueryWrapper<OrderEntity>().eq("id", orderEntity.getId()).eq("uid", orderEntity.getUid()));
        if (order == null || order.getStatus() != 0) {
            log.info("uid->{} id->{}  status->{}", orderEntity.getUid(), order.getId(),  order == null ? null : order.getStatus());
            return OrderErrorEnum.ERROR_NOT_FOUND;
        }
        if (System.currentTimeMillis() - order.getCtime() < 10 * 60000) {
            log.info("uid->{} id->{}  status->{} ", orderEntity.getUid(), order.getId(), order == null ? null : order.getStatus());
            return OrderErrorEnum.ERROR_NOT_MATCH;
        }
        this.getBaseMapper().update(null, new UpdateWrapper<OrderEntity>().eq("id", orderEntity.getId()).set("status", -1));
        return null;
    }

    /**
     * 查询用户的订单列表
     *
     * @param uid
     * @return
     */
    public PagingBase<OrderEntity> listOrder(long uid, QueryPage queryPage) {
        Page<OrderEntity> iPage = new Page(queryPage.getPageSize(), queryPage.getPageNum());
        IPage<OrderEntity> pageList = this.page(iPage, new QueryWrapper<OrderEntity>().eq("uid", uid));
        return new PagingBase<OrderEntity>(pageList.getRecords(), pageList.getTotal());
    }

    /**
     * 从币安拉取行情
     *
     * @return
     */
    public List<CandlestickDto> getCandlestickList(String symbol, String interval, int size) {
        List<Candlestick> list = CandlesticksCache.getInstance().getCandlestickBars(symbol, interval, size);
        List<CandlestickDto> result = new ArrayList<>();
        for (Candlestick candlestick : list) {
            result.add(CandlestickDto.builder().close(candlestick.getClose())
                    .closeTime(candlestick.getCloseTime())
                    .high(candlestick.getHigh())
                    .low(candlestick.getLow())
                    .numberOfTrades(candlestick.getNumberOfTrades())
                    .open(candlestick.getOpen())
                    .openTime(candlestick.getOpenTime())
                    .quoteAssetVolume(candlestick.getQuoteAssetVolume())
                    .takerBuyBaseAssetVolume(candlestick.getTakerBuyBaseAssetVolume())
                    .takerBuyQuoteAssetVolume(candlestick.getTakerBuyQuoteAssetVolume())
                    .volume(candlestick.getVolume()).build());
        }
        return result;
    }
}
