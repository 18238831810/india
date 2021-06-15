package com.cf.crs.controller;


import com.cf.crs.common.constant.MsgError;
import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.*;
import com.cf.crs.service.AccountBalanceService;
import com.cf.crs.service.OrderCashoutService;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public ResultJson<AccountBalanceEntity> getAccountBalanceByUId(Long uid){
        //return HttpWebResult.getMonoError(MsgError.AUTH_FAIL);
        return HttpWebResult.getMonoSucResult(accountBalanceService.getAccountBalanceByUId(uid));
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
