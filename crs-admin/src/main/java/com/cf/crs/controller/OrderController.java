package com.cf.crs.controller;

import com.binance.api.client.constant.OrderErrorEnum;
import com.binance.api.client.domain.market.Candlestick;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.entity.QueryPage;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.service.OrderCommissionServiceImpl;
import com.cf.crs.service.OrderServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "下单操作")
@RestController
@RequestMapping("/order")
public class OrderController extends  BaseController{

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    OrderCommissionServiceImpl orderCommissionService;

    @PostMapping("/save")
    @ApiOperation("保存下单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomId", value = "房间号ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "token", value = "下单人的token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "payment", value = "下单金额", required = true, dataType = "double"),
            @ApiImplicitParam(name = "buyDirection", value = "下单方向(rise/fall/equal)", required = true, dataType = "String")
    })
    public Result saveOrder(OrderEntity orderEntity) {
        orderEntity.setUid(getUidFromToken(orderEntity.getToken()));
        OrderErrorEnum orderErrorEnum = orderService.saveUserOrder(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
    }

    @PostMapping("/cancel")
    @ApiOperation("撤单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "long"),
            @ApiImplicitParam(name = "token", value = "下单人的token", required = true, dataType = "String")
    })
    public Result delOrder(OrderEntity orderEntity) {
        orderEntity.setUid(getUidFromToken(orderEntity.getToken()));
        OrderErrorEnum orderErrorEnum = orderService.updateOrderToDelStatus(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
    }

    @PostMapping("/list")
    @ApiOperation("订单列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "token", value = "下单人的token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int" ,defaultValue = "1"),
            @ApiImplicitParam(name = "pageSize", value = "每页显示多少条", required = true, dataType = "int" ,defaultValue = "15")
    })
    public Result listOrder(String token, QueryPage queryPage) {
        PagingBase<OrderEntity> pagingBase = orderService.listOrder(getUidFromToken(token),queryPage);
        return new Result<PagingBase>().ok(pagingBase);
    }

    @PostMapping("/saveCommission")
    @ApiOperation("生成直播室的提成数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "day", value = "生成指定多少天前的一天数据", required = true, dataType = "Integer",defaultValue = "null"),
    })
    public Result saveCommission(Integer day)
    {
       int total= orderCommissionService.saveOrderCommission(day);
       return new Result<>().error(200, "总条数:"+total);
    }

    @PostMapping("/candlestick")
    @ApiOperation("行情数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对", required = true, dataType = "String",defaultValue = "btcusdt"),
            @ApiImplicitParam(name = "interval", value = "行情周期", required = true, dataType = "String",defaultValue = "1d"),
    })
    public Result getCandlestick(String symbol ,String interval)
    {
        List<Candlestick> result= orderService.getCandlestickList(symbol,interval);
        return new Result<>().ok(result);
    }
}
