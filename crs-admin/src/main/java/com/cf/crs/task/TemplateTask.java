package com.cf.crs.task;

import com.cf.crs.job.task.ITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 考评计划任务
 * @author frank
 * 2019/10/16
 **/
@Slf4j
@Component("templateTask")
public class TemplateTask implements ITask{

    @Override
    public void run(String params) {
        try {
            //计划任务

            log.info("同步告警计划开始执行");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

}
