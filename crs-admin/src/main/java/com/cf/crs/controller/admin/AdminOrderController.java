package com.cf.crs.controller.admin;


import com.cf.crs.common.entity.PagingBase;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.OrderDto;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.service.OrderServiceImpl;
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
@Api(tags="用户交易记录")
@RestController
@RequestMapping("/admin/order")
public class AdminOrderController {

    @Autowired
    OrderServiceImpl orderService;

    @PostMapping("/queryList")
    public Result<PagingBase<OrderEntity>> list(OrderDto dto){
        PagingBase<OrderEntity> pagingBase = orderService.queryList(dto);
        return new Result<PagingBase<OrderEntity>>().ok(pagingBase);
    }

    @PostMapping("/queryTotalAmount")
    public ResultJson<BigDecimal> queryTotalAmount(OrderDto dto){
        return orderService.queryTotalAmount(dto);
    }

    @GetMapping("/export")
    public void export(OrderDto dto) {
        orderService.export(dto);
    }
}
