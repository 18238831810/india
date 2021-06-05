package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import com.cf.crs.service.OrderCommissionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("orderCommissionTask")
public class OrderCommissionTask  implements ITask {
    @Autowired
    OrderCommissionServiceImpl orderCommissionService;


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
        orderCommissionService.saveOrderCommission();
    }
}
