package com.cf.crs.controller;


import com.cf.crs.entity.AccountBalanceEntity;
import com.cf.crs.entity.OrderCashoutDto;
import com.cf.crs.service.AccountBalanceService;
import com.cf.crs.service.OrderCashoutService;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Api(tags="提现")
@RestController
@RequestMapping("/orderCashout")
public class OrderCashoutController {

    @Autowired
    OrderCashoutService orderCashoutService;

    @Autowired
    AccountBalanceService accountBalanceService;

    @PostMapping("/getAccountBalanceByUId")
    @ApiOperation("获取用户余额")
    public ResultJson<AccountBalanceEntity> getAccountBalanceByUId(long uid){
        return HttpWebResult.getMonoSucResult(accountBalanceService.getAccountBalanceByUId(uid));
    }

    @PostMapping("/order")
    @ApiOperation("提现下单")
    public ResultJson<String> order(OrderCashoutDto orderCashoutDto){
        return orderCashoutService.order(orderCashoutDto);
    }

    @PostMapping("/approve")
    @ApiOperation("提现审批")
    public ResultJson<String> approve(Long id){
        return orderCashoutService.approve(id);
    }
}
