package com.cf.crs.service;

import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.constant.CandlestickDto;
import com.cf.crs.common.utils.DateUtils;
import com.cf.util.utils.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;

@Service
@Slf4j
public class CandlestickService {

    @Async
    void cache()
    {
        while(true)
        {
            long now =DateUtils.getZeroSecondMils(System.currentTimeMillis());
            try {
                doCache(now);
            }
            catch (Exception e)
            {
                log.error(e.getMessage());
            }finally {
                sleep(1000);
             }
        }
    }

    /**
     * 开始缓存处理
     */
    private CandlestickDto  doCache(long now)
    {
            if(LocalDateTime.now().getSecond()<=45)
            {
                CandlestickDto candlestickDto= CandlesticksCache.getInstance().getCandlestickDto(now);
                if(candlestickDto==null)
                {
                    CandlesticksCache.getInstance().cacheBtcOneMinu();
                    sleep(500);
                    doCache(now);
                }
                return candlestickDto;
            }
            return null;
    }

    private void sleepBySecond()
    {
        if(LocalDateTime.now().getSecond()<=10)
        {
            sleep(3000);
        }
        else sleep(10000);
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
