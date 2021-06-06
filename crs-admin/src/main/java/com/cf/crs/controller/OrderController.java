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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api(tags = "订单操作")
@RestController
@RequestMapping("/order")
public class OrderController extends  BaseController{

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    OrderCommissionServiceImpl orderCommissionService;

    @PostMapping("/save")
    @ApiOperation("保存订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "roomId", value = "房间号ID", required = true, dataType = "String"),
            @ApiImplicitParam(name = "token", value = "下单人的token", required = true, dataType = "String"),
            @ApiImplicitParam(name = "payment", value = "下单金额", required = true, dataType = "double"),
            @ApiImplicitParam(name = "buyDirection",allowableValues = "rise,fall,equal",value = "下单方向", required = true, dataType = "String")
    })
    public Result saveOrder(OrderEntity orderEntity) {
        orderEntity.setUid(getUidFromToken(orderEntity.getToken()));
        OrderErrorEnum orderErrorEnum = orderService.saveUserOrder(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
    }

    @PostMapping("/cancel")
    @ApiOperation("撤单订单")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "订单ID", required = true, dataType = "long"),
            @ApiImplicitParam(name = "token", value = "下单人的token", required = true, dataType = "String")
    })
    public Result delOrder(OrderEntity orderEntity) {
        orderEntity.setUid(getUidFromToken(orderEntity.getToken()));
        OrderErrorEnum orderErrorEnum = orderService.updateOrderToDelStatus(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
    }

    @GetMapping("/list")
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
    @ApiOperation("生成直播提成")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "day", value = "生成指定多少天前的一天数据", required = true, dataType = "Integer",defaultValue = "null"),
    })
    public Result saveCommission(Integer day)
    {
       int total= orderCommissionService.saveOrderCommission(day);
       return new Result<>().error(200, "总条数:"+total);
    }

    @GetMapping("/candlestick")
    @ApiOperation("行情数据")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "symbol", value = "交易对", required = true, dataType = "String",defaultValue = "btcusdt"),
            @ApiImplicitParam(name = "interval",  allowableValues ="1m,3m,5m,15m,30m,1h,2h,4h,6h,8h,12h,1d", value = "行情周期", required = true, dataType = "String",defaultValue = "1d"),
            @ApiImplicitParam(name = "size",   value = "行情周期", dataType = "int",defaultValue = "240"),
    })
    public Result getCandlestick(String symbol ,String interval,Integer size)
    {
        List<Candlestick> result= orderService.getCandlestickList(symbol,interval,size==null?240:size);
        return new Result<>().ok(result);
    }
}
