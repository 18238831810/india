package com.cf.crs.controller;

import com.binance.api.client.constant.OrderErrorEnum;
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

@Api(tags = "下单操作")
@RestController
@RequestMapping("/order")
public class OrderController {

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
        OrderErrorEnum orderErrorEnum = orderService.saveUserOrder(orderEntity);
        return orderErrorEnum == null ? new Result<>() : new Result<>().error(orderErrorEnum.getCode(), orderErrorEnum.getError());
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
}
