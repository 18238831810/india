package com.binance.api.client;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Illustrates how to use the klines/candlesticks event stream to create a local cache of bids/asks for a symbol.
 */

public class CandlesticksCache {
    /**
     * Key is the start/open time of the candle, and the value contains candlestick date.
     */

    private static CandlesticksCache candlesticksCache = null;
    private static int maximumSize = 100;
    /**
     * 限制Map大小为maximumSize
     */
    private static LinkedHashMap<Long, Candlestick> cachLinkMap = new LinkedHashMap<Long, Candlestick>() {

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Candlestick> eldest) {
            return size() > maximumSize;
        }
    };

    private CandlesticksCache() {
    }

    public static CandlesticksCache getInstance() {
        if (candlesticksCache == null) candlesticksCache = new CandlesticksCache();
        return candlesticksCache;
    }

    public synchronized int cache() {
        return cache("btcusdt", CandlestickInterval.ONE_MINUTE);
    }


    public int cache(String symbol, CandlestickInterval interval) {
        return initializeCandlestickCache(symbol, interval);
        //startCandlestickEventStreaming(symbol, interval);
    }

    /**
     * Initializes the candlestick cache by using the REST API.
     */
    private int initializeCandlestickCache(String symbol, CandlestickInterval interval) {
        List<Candlestick> candlestickBars = getCandlestickBars(symbol, interval,null);
        int size = candlestickBars.size();
        for (int i = (size > maximumSize ? (size - maximumSize) : 0); i < size; i++) {
            Candlestick candlestickBar = candlestickBars.get(i);
            cachLinkMap.put(candlestickBar.getOpenTime(), candlestickBar);
        }
        return size;
    }

    public List<Candlestick> getCandlestickBars(String symbol, CandlestickInterval interval,Integer size) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List list = client.getCandlestickBars(symbol.toUpperCase(), interval);
        if(size==null) return list;
        return list.size()>size?list.subList(list.size()-size,list.size()):list;
    }
    public List<Candlestick> getCandlestickBars(String symbol, String interval,int size) {
        return getCandlestickBars( symbol, getInterval(interval),size);
    }
    public List<Candlestick> getCandlestickBars(Integer size) {
        return getCandlestickBars("btcusdt", CandlestickInterval.ONE_MINUTE,size);
    }
    private CandlestickInterval getInterval(String inteval) {
        switch (inteval) {
            case "1m":
                return CandlestickInterval.ONE_MINUTE;
            case "3m":
                return CandlestickInterval.THREE_MINUTES;
            case "5m":
                return CandlestickInterval.FIVE_MINUTES;
            case "15m":
                return CandlestickInterval.FIFTEEN_MINUTES;
            case "30m":
                return CandlestickInterval.HALF_HOURLY;
            case "1h":
                return CandlestickInterval.HOURLY;
            case "2h":
                return CandlestickInterval.TWO_HOURLY;
            case "4h":
                return CandlestickInterval.FOUR_HOURLY;
            case "6h":
                return CandlestickInterval.SIX_HOURLY;
            case "8h":
                return CandlestickInterval.EIGHT_HOURLY;
            case "12h":
                return CandlestickInterval.TWELVE_HOURLY;
            default:
                return CandlestickInterval.DAILY;
        }

    }

    /**
     * Begins streaming of depth events.
     */
    private void startCandlestickEventStreaming(String symbol, CandlestickInterval interval) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiWebSocketClient client = factory.newWebSocketClient();

        client.onCandlestickEvent(symbol.toLowerCase(), interval, response -> {
            Long openTime = response.getOpenTime();
            Candlestick updateCandlestick = cachLinkMap.get(openTime);
            if (updateCandlestick == null) {
                return;
            }
            updateCandlestick.setOpenTime(response.getOpenTime());
            updateCandlestick.setOpen(response.getOpen());
            updateCandlestick.setLow(response.getLow());
            updateCandlestick.setHigh(response.getHigh());
            updateCandlestick.setClose(response.getClose());
            updateCandlestick.setCloseTime(response.getCloseTime());
            updateCandlestick.setVolume(response.getVolume());
            updateCandlestick.setNumberOfTrades(response.getNumberOfTrades());
            updateCandlestick.setQuoteAssetVolume(response.getQuoteAssetVolume());
            updateCandlestick.setTakerBuyQuoteAssetVolume(response.getTakerBuyQuoteAssetVolume());
            updateCandlestick.setTakerBuyBaseAssetVolume(response.getTakerBuyQuoteAssetVolume());

            // Store the updated candlestick in the cache
            cachLinkMap.put(openTime, updateCandlestick);
        });
    }

    /**
     * @return a klines/candlestick cache, containing the open/start time of the candlestick as the key,
     * and the candlestick data as the value.
     */
    public Map<Long, Candlestick> getCandlesticksCache() {
        return cachLinkMap;
    }

    public static void main(String[] args) {
        CandlesticksCache.getInstance().getCandlestickBars(null);
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class MyCandlestick extends  Candlestick
    {

    }
}
