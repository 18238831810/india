package com.cf.crs.task;

import com.binance.api.client.CandlesticksCache;
import com.cf.crs.job.task.ITask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component("cacheCandleTask")
public class CacheCandleTask  implements ITask {
    @Override
    public void run(String params){
        try {
            if(log.isDebugEnabled())
                 log.debug("TestTask定时任务正在执行，参数为：{}", params);
            cacheCandlesticks(params);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     * 定时同步行情数据
     * @param params
     */
    private void cacheCandlesticks(String params) {
        CandlesticksCache.getInstance().cache();
    }
}
