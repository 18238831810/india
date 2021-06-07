package com.cf.crs.controller.admin;


import com.cf.crs.service.OrderCashoutService;
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
@RequestMapping("/admin/cashout")
public class CashoutController {

    @Autowired
    OrderCashoutService orderCashoutService;


    @PostMapping("/approve")
    @ApiOperation("提现审批")
    public ResultJson<String> approve(Long id){
        return orderCashoutService.approve(id);
    }
}
