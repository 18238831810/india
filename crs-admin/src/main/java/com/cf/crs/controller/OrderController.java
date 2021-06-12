package com.cf.crs.controller;

import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.constant.CandlestickDto;
import com.cf.crs.common.constant.OrderErrorEnum;
import com.binance.api.client.domain.market.Candlestick;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.entity.QueryPage;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.service.CandlestickService;
import com.cf.crs.service.OrderCommissionServiceImpl;
import com.cf.crs.service.OrderLeverServiceImpl;
import com.cf.crs.service.OrderServiceImpl;
import com.cf.util.utils.Const;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.jsoup.helper.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "订单操作")
@RestController
@RequestMapping("/api/order")
public class OrderController extends BaseController {

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    OrderCommissionServiceImpl orderCommissionService;
    @Autowired
    OrderLeverServiceImpl orderLeverService;
    @Autowired
    CandlestickService candlestickService;

    @PostMapping("/save")
    @ApiOperation("保存订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomId", value = "房间号ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "payment", value = "下单金额", required = true, dataType = "double"),
            @ApiImplicitParam(name = "buyDirection", allowableValues = "rise,fall,equal", value = "下单方向", required = true, dataType = "String")
    })
    public Result saveOrder(OrderEntity orderEntity) {
        orderEntity.setUid(getUid());
        OrderErrorEnum orderErrorEnum = orderService.saveUserOrder(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
    }

    @PostMapping("/cancel")
    @ApiOperation("撤单订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "long"),
    })
    public Result delOrder(int id) {
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setUid(getUid());
        orderEntity.setId(id);
        OrderErrorEnum orderErrorEnum = orderService.updateOrderToDelStatus(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
    }

    @GetMapping("/list")
    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码,默认1", dataType = "int", defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示多少条,默认15", dataType = "int", defaultValue = "15"),
            @ApiImplicitParam(name = "status", value = "类型（0：交易中 1：已成交 2：已撤销）不传，则是接取所有", dataType = "int", defaultValue = "15")
    })
    public Result listOrder( QueryPage queryPage,Integer status) {
        PagingBase<OrderEntity> pagingBase = orderService.listOrder(getUid(), status,queryPage);
        return new Result<PagingBase>().ok(pagingBase);
    }

    @PostMapping("/saveCommission")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "day", value = "生成指定多少天前的一天数据", dataType = "Integer", defaultValue = "null"),
    })
    public Result saveCommission(Integer day) {
        int total = orderCommissionService.saveOrderCommission(day);
        return new Result<>().error(200, "总条数:" + total);
    }

    @GetMapping("/buyDirectLever")
    @ApiOperation("赔率列表")
    public Result getSetting() {
        return new Result<>().ok(orderLeverService.getBuyDirectLever());
    }

    @GetMapping("/candlestick")
    @ApiOperation("行情数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对,默认btcusdt", required = true, dataType = "String", defaultValue = "btcusdt"),
            @ApiImplicitParam(name = "interval", allowableValues = "1m,3m,5m,15m,30m,1h,2h,4h,6h,8h,12h,1d", value = "行情周期,默认1m", dataType = "String", defaultValue = "1m"),
            @ApiImplicitParam(name = "size", value = "行情条数,默认240条", dataType = "int", defaultValue = "240"),
    })
    public Result<Collection<CandlestickDto>> getCandlestick(String symbol, String interval, Integer size) {
        Collection<CandlestickDto> result = candlestickService.getCandlestickList(StringUtil.isBlank(symbol) ? "btcusdt" : symbol, StringUtil.isBlank(interval) ? "1m" : interval, size == null ? 240 : size);
        return new Result<Collection<CandlestickDto>>().ok(result);
    }

    @GetMapping("/dayprofit")
    @ApiOperation("盈亏统计")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "start", value = "开始时间(>=)", dataType = "long"),
            @ApiImplicitParam(name = "end", value = "结束时间(<)", dataType = "long")
    })
    public Result dayProfit(long start ,long end ) {
        List<Map<String, Object>> result = orderService.getProfitByDay(start,end,getUid());
        return new Result<>().ok(result);
    }

    @GetMapping("/systime")
    @ApiOperation("系统时间")
    public Result<Map<String,Object>> systemTimeSetting() {
        Map<String,Object> map = new HashMap();
        map.put("time",System.currentTimeMillis());
        map.put("limit_second", Const.LIMIT_TIME);
        return new Result<Map<String, Object>>().ok(map);
    }


}
