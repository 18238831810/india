package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.OrderCashinDto;
import com.cf.crs.entity.OrderCashinEntity;
import com.cf.crs.service.OrderCashinService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@Slf4j
@Api(tags="充值")
@RestController
@RequestMapping("/admin/cashin")
public class AdminOrderCashinController {

    @Autowired
    OrderCashinService orderCashinService;


    @PostMapping("/queryList")
    public Result<PagingBase<OrderCashinEntity>> list(OrderCashinDto dto){
        PagingBase<OrderCashinEntity> pagingBase = orderCashinService.queryList(dto);
        return new Result<PagingBase<OrderCashinEntity>>().ok(pagingBase);
    }

    @PostMapping("/queryTotalAmount")
    public ResultJson<BigDecimal> queryTotalAmount(OrderCashinDto dto){
        return orderCashinService.queryTotalAmount(dto);
    }

    @GetMapping("/export")
    public void export(OrderCashinDto dto) {
        orderCashinService.export(dto);
    }
}
