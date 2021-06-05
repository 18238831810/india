package com.cf.crs.controller;


import com.cf.crs.properties.OrderCallbackParam;
import com.cf.crs.service.OrderCashinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/orderCallback")
public class OrderCallbackController {

    @Autowired
    OrderCashinService orderCashinService;

    /**
     * 存款掉
     * @param callbackParamm
     * @return
     */
    @PostMapping("/order")
    public String order(OrderCallbackParam callbackParamm){
        return orderCashinService.orderCallback(callbackParamm);
    }
}
