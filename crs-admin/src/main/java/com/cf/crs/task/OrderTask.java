package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import com.cf.crs.service.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单处理
 */
@Slf4j
@Component("orderTask")
public class OrderTask implements ITask {

    @Autowired
    OrderServiceImpl orderService;


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
        orderService.updateOrder();
    }


}
