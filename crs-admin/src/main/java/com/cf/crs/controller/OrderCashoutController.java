package com.cf.crs.controller;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderCashoutDto;
import com.cf.crs.entity.OrderCashoutEntity;
import com.cf.crs.entity.OrderCashoutParam;
import com.cf.crs.service.AccountBalanceService;
import com.cf.crs.service.OrderCashoutService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

@Slf4j
@Api(tags="提现")
@RestController
@RequestMapping("/api/orderCashout")
public class OrderCashoutController {

    @Autowired
    OrderCashoutService orderCashoutService;

    @Autowired
    AccountBalanceService accountBalanceService;

    @GetMapping("/getAccountBalanceByUId")
    @ApiOperation("获取用户余额")
    public ResultJson<AccountBalanceEntity> getAccountBalanceByUId(@ApiIgnore Long uid){
        return accountBalanceService.getAccountBalanceByUId(uid);
    }

    @PostMapping("/order")
    @ApiOperation("提现下单")
    public ResultJson<String> order(OrderCashoutParam orderCashoutParam){
        return orderCashoutService.order(orderCashoutParam);
    }

    @GetMapping("/list")
    @ApiOperation("提现下单列表查询")
    public Result<PagingBase<OrderCashoutEntity>> list(OrderCashoutDto dto){
        PagingBase<OrderCashoutEntity> pagingBase = orderCashoutService.list(dto);
        return new Result<PagingBase<OrderCashoutEntity>>().ok(pagingBase);
    }

}
