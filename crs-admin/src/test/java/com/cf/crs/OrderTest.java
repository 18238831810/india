package com.cf.crs;

import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.constant.CandlestickDto;
import com.binance.api.client.domain.market.Candlestick;
import com.cf.AdminApplication;
import com.cf.crs.common.utils.Result;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.service.OrderCommissionServiceImpl;
import com.cf.crs.service.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = AdminApplication.class)
public class OrderTest {

    @Autowired
    OrderServiceImpl orderService;

    @Autowired
    OrderCommissionServiceImpl orderCommissionService;


    @Test
    public void testSave()
    {
        OrderEntity o = new OrderEntity();
        o.setRoomId("001");
        o.setPayment(3000);
        o.setBuyDirection("rise");
        orderService.saveUserOrder(o);
    }

    @Test
    public void testUpdate()
    {
        orderService.updateOrder();
    }

    @Test
    public void testOrderCommissionService()
    {
        orderCommissionService.saveOrderCommission(1);
    }

    @Test
    public void testCandlestickInterval()
    {
        orderCommissionService.saveOrderCommission(1);
    }

    @Test
    public void testCandlestickBars()
    {

        Map<Long, CandlestickDto> result=  CandlesticksCache.getInstance().getBianaceBTCCandlesticksCache();
        Result result1=  new Result<>().ok(result);
        System.out.println(result);
    }


}
