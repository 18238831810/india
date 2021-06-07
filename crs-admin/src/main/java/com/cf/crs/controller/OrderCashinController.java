package com.cf.crs.controller;


import com.cf.crs.entity.OrderCashinDto;
import com.cf.crs.service.OrderCashinService;
import com.cf.util.http.ResultJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResultJson<String> order(OrderCashinDto orderCashinDto){
        return orderCashinService.order(orderCashinDto);
    }
}
