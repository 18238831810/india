package com.cf.crs;

import com.cf.AdminApplication;
import com.cf.crs.entity.OrderEntity;
import com.cf.crs.service.OrderCommissionServiceImpl;
import com.cf.crs.service.OrderServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

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
        o.setToken("abcd123");
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
        orderCommissionService.saveOrderCommission();
    }
}
