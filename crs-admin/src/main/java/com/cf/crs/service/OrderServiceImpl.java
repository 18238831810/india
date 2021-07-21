package com.cf.crs.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.binance.api.client.constant.CandlestickDto;
import com.binance.api.client.domain.market.CandlestickInterval;
import com.cf.crs.common.constant.OrderErrorEnum;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.entity.QueryPage;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.common.utils.DateUtils;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderDto;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.entity.OrderLeverEntity;
import com.cf.crs.mapper.OrderMapper;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.Const;
import com.cf.util.utils.ExcelUtils;
import com.cf.util.utils.NumberUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletResponse;
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
    FinancialDetailsService financialDetailsService;

    @Autowired
    AccountBalanceService accountBalanceService;

    @Autowired
    CandlestickService candlestickService;

    @Autowired
    HttpServletResponse response;

    @Autowired
    SendRedisMessage sendRedisMessage;

    /**
     * 下单的三个方向常量
     */
    private final static List<String> buyDirect = Arrays.asList("rise", "fall", "equal");


    @Transactional
    public OrderErrorEnum saveUserOrder(OrderEntity orderEntity) throws RenException {

        OrderErrorEnum orderErrorEnum = filterParam(orderEntity);
        if (orderErrorEnum != null)
            return orderErrorEnum;

        orderEntity.setEarlyStageTime(getTimeKey(orderEntity.getCtime()));
        return saveOrder(orderEntity);
    }

    private OrderErrorEnum filterParam(OrderEntity orderEntity) {
        int second = LocalDateTime.now().getSecond();
        if (second >= Const.LIMIT_TIME) {
            log.warn("已经过时了 second->{},uid->{}", second, orderEntity.getUid());
            return OrderErrorEnum.ERROR_OVER_TIME;
        }
        orderEntity.setCtime(System.currentTimeMillis());

        if (StringUtils.isEmpty(orderEntity.getBuyDirection())
                || (!buyDirect.contains(orderEntity.getBuyDirection()))) {
            log.warn("direct->{} uid->{}", orderEntity.getBuyDirection(), orderEntity.getUid());
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
        OrderErrorEnum orderErrorEnum= accountBalanceService.updateAmountFromOrder(orderEntity);
        if(orderErrorEnum!=null) return orderErrorEnum;
        orderEntity.setNextStageTime(orderEntity.getEarlyStageTime() + 60000);
        orderEntity.setMarketCycle(CandlestickInterval.ONE_MINUTE.getIntervalId());
        this.save(orderEntity);

//        financialDetailsService.save(FinancialDetailsEntity.builder()
//                .amount((float) orderEntity.getProfit())
//                .type(3)
//                .orderTime(orderEntity.getCtime())
//                .orderSn(orderEntity.getId()+"").build());

        return null;
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
        CandlestickDto earlyStage = candlestickService.getCandlestick(orderEntity.getEarlyStageTime());
        if (earlyStage == null) {
            //log.warn("获取earlyStage行情时获取不到->{} id->{}", orderEntity.getEarlyStageTime(), orderEntity.getId());
            return 0;
        }
        orderEntity.setEarlyStagePrice(getPointPrize(earlyStage.getOpen()));
        orderEntity.setEarlyStageTime(earlyStage.getOpenTime());

        CandlestickDto nextStage = candlestickService.getCandlestick(orderEntity.getNextStageTime());
        if (nextStage == null) {
           // log.warn("获取nextStage行情时获取不到->{},id->{}", orderEntity.getNextStageTime(), orderEntity.getId());
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
            AccountBalanceEntity accountBalanceEntity = accountBalanceService.getAccountBalanceByUid(orderEntity.getUid());
            accountBalanceEntity.setAmount(NumberUtils.add(orderEntity.getProfit(),orderEntity.getPayment()));
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
    @Transactional
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
        accountBalanceService.updateBalance(AccountBalanceEntity.builder().amount(new BigDecimal(Double.toString(order.getPayment()))).updateTime(System.currentTimeMillis()).build());
        return null;
    }

    /**
     * 查询用户的订单列表
     *
     * @param uid
     * @return
     */
    public PagingBase<OrderEntity> listOrder(long uid,Integer status, QueryPage queryPage) {
        Page<OrderEntity> iPage = new Page(queryPage.getPageNum(), queryPage.getPageSize());
        QueryWrapper<OrderEntity> queryWrapper= new QueryWrapper<OrderEntity>().eq("uid", uid).orderByDesc("ctime");
        if(status!=null)
        {
            queryWrapper.eq("status",status);
        }
        IPage<OrderEntity> pageList = this.page(iPage,queryWrapper);
        return new PagingBase<>(pageList.getRecords(), pageList.getTotal());
    }



    @PostConstruct
    public  void initCacheCandlestick()
    {
        log.info("===============init  binance candlestick cache  ================");
        candlestickService.cache();
    }




    /**
     * 按时间 区间统计盈亏情况
     * @param start
     * @param end
     */
    public List<Map<String, Object>>  getProfitByDay(long start ,long end,long uid) {
        return  this.getBaseMapper().selectMaps(new QueryWrapper<OrderEntity>()
                .ge("ctime",start)
                .lt("ctime",end).eq("uid",uid)
        .select("sum(profit) as profit","uid","from_unixtime(ctime/1000,'%Y-%m-%d') as time")
        .groupBy("uid","from_unixtime(ctime/1000,'%Y-%m-%d')"));
    }

    /**
     * 分页查询用户的资金列表
     *
     * @param dto
     * @return
     */
    public PagingBase<OrderEntity> queryList(OrderDto dto) {
        Page<OrderEntity> iPage = new Page(dto.getPageNum(), dto.getPageSize());
        QueryWrapper<OrderEntity> queryWrapper = getQueryWrapper(dto);
        queryWrapper.orderByDesc("ctime");
        IPage<OrderEntity> pageList = this.page(iPage, queryWrapper);
        List<OrderEntity> records = pageList.getRecords();
        return new PagingBase<OrderEntity>(records, pageList.getTotal());
    }

    private QueryWrapper<OrderEntity> getQueryWrapper(OrderDto dto) {
        return new QueryWrapper<OrderEntity>().eq(dto.getUid() != null,"uid", dto.getUid())
                     .ge(dto.getStartTime() != null,"ctime",dto.getStartTime())
                    .le(dto.getEndTime() != null,"ctime",dto.getEndTime());
    }

    /**
     * 统计盈亏总额
     *
     * @param dto
     * @return
     */
    public ResultJson<BigDecimal> queryTotalAmount(OrderDto dto) {
        QueryWrapper<OrderEntity> queryWrapper = getQueryWrapper(dto);
        queryWrapper.select(" sum(profit) as profit");
        OrderEntity orderEntity = baseMapper.selectOne(queryWrapper);
        return HttpWebResult.getMonoSucResult(orderEntity.getProfit());
    }

    public void export(OrderDto dto) {
        try {
            QueryWrapper<OrderEntity> queryWrapper = getQueryWrapper(dto);
            List<OrderEntity> list = baseMapper.selectList(queryWrapper);
            ExcelUtils.exportExcelWithDict(response, null, list, OrderEntity.class);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public ResultJson<BigDecimal> sendMessage(Long id) {
        OrderEntity orderEntity = baseMapper.selectById(id);
        sendRedisMessage.send(orderEntity,Const.ORDER_TAG);
        return HttpWebResult.getMonoSucStr();
    }

    public ResultJson<BigDecimal> sendProfitMessage(Long id) {
        OrderEntity orderEntity = baseMapper.selectById(id);
        sendRedisMessage.send(orderEntity,Const.ORDER_PROFIT_TAG);
        return HttpWebResult.getMonoSucStr();
    }

}
