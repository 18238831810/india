package com.cf.crs.controller;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.*;
import com.cf.crs.service.OrderCashinService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="存款")
@RestController
@RequestMapping("/api/orderCashin")
public class OrderCashinController {

    @Autowired
    OrderCashinService orderCashinService;

    @PostMapping("/order")
    @ApiOperation("存款下单")
    public ResultJson<String> order(OrderCashinParam orderCashinParam){
        return orderCashinService.order(orderCashinParam);
    }


    @GetMapping("/list")
    @ApiOperation("存款下单列表查询")
    public Result<PagingBase<OrderCashinEntity>> list(OrderCashinDto dto){
        PagingBase<OrderCashinEntity> pagingBase = orderCashinService.list(dto);
        return new Result<PagingBase<OrderCashinEntity>>().ok(pagingBase);
    }
}
