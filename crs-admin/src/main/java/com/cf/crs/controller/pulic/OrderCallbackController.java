package com.cf.crs.controller.pulic;


import com.cf.crs.properties.CollectionCallbackParam;
import com.cf.crs.properties.OrderCallbackParam;
import com.cf.crs.service.OrderCashinService;
import com.cf.crs.service.OrderCashoutService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 回调
 */
@Slf4j
@RestController
@RequestMapping("/public/orderCallback")
public class OrderCallbackController {

    @Autowired
    OrderCashinService orderCashinService;

    @Autowired
    OrderCashoutService orderCashoutService;

    /**
     * 存款回调
     * @param callbackParamm
     * @return
     */
    @PostMapping("/order")
    public String order(OrderCallbackParam callbackParamm){
        return orderCashinService.orderCallback(callbackParamm);
    }
    /**
     * 取款回调
     * @param callbackParamm
     * @return
     */
    @PostMapping("/collection")
    public String collction(CollectionCallbackParam callbackParamm){
        return orderCashoutService.orderCallback(callbackParamm);
    }
}
