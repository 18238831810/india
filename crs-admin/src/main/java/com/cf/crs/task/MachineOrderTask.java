package com.cf.crs.task;

import com.cf.crs.entity.OrderEntity;
import com.cf.crs.job.task.ITask;
import com.cf.crs.service.OrderCommissionServiceImpl;
import com.cf.crs.service.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Component("machineOrderTask")
public class MachineOrderTask  implements ITask {
    @Autowired
    OrderServiceImpl orderService;
    private final static List<String> buyDirect = Arrays.asList("rise", "fall", "equal");

    @Override
    public void run(String params) {
        try {
            triggerTask(params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private void triggerTask(String param)
    {
        orderService.saveUserOrder(getEntity());
    }

    private OrderEntity getEntity()
    {
        OrderEntity o = new OrderEntity();
        o.setRoomId("000");
        o.setToken(UUID.randomUUID().toString());
        o.setPayment(new Random().nextInt(2000) + 3000);
        o.setBuyDirection(buyDirect.get(new Random().nextInt(3))+"");
        return o;
    }
}
