package com.cf.crs.service;

import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.constant.CandlestickDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Collection;

@Service
@Slf4j
public class CandlestickService {

    @Async
    void cache()
    {
        while(true)
        {
            doCache();
        }
    }

    /**
     * 开始缓存处理
     */
    private void  doCache()
    {
        try
        {
            CandlesticksCache.getInstance().cacheBtcOneMinu();
        }catch (Exception e)
        {
            log.error(e.getMessage());
            sleep(2000);
        }finally {
            sleep(5000);
        }
    }

    private void sleep( long time)
    {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * 从币安拉取行情
     *
     * @return
     */
    public Collection<CandlestickDto> getCandlestickList(String symbol, String interval, int size) {
        return CandlesticksCache.getInstance().getCandlestickDto(symbol, interval, size);

    }

    /**
     * 根据实时获取当时的行情
     *
     * @param timeKey
     * @return
     */
    public CandlestickDto getCandlestick(Long timeKey) {
        return CandlesticksCache.getInstance().getBianaceBTCCandlesticksCache().get(timeKey);
    }
}
