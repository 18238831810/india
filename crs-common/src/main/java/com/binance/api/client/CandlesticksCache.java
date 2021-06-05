package com.binance.api.client;

import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;

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
    private static int maximumSize = 20;
    /**
     * 限制Map大小为maximumSize
     */
    private static LinkedHashMap<Long, Candlestick> cachLinkMap= new LinkedHashMap<Long, Candlestick>() {

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, Candlestick> eldest) {
            return size() > maximumSize;
        }
    };

    private CandlesticksCache() {
    }

    public static CandlesticksCache  getInstance()
    {
        if(candlesticksCache==null) candlesticksCache=new CandlesticksCache();
        return candlesticksCache;
    }

    public synchronized int cache(){
       return  cache("btcusdt", CandlestickInterval.ONE_MINUTE);
    }


    public int  cache(String symbol, CandlestickInterval interval)
    {
      return  initializeCandlestickCache(symbol, interval);
        //startCandlestickEventStreaming(symbol, interval);
    }

    /**
     * Initializes the candlestick cache by using the REST API.
     */
    private int initializeCandlestickCache(String symbol, CandlestickInterval interval) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<Candlestick> candlestickBars = client.getCandlestickBars(symbol.toUpperCase(), interval);
        int size =candlestickBars.size();
        for (int i=(size>maximumSize?(size-maximumSize):0);i<size;i++) {
            Candlestick candlestickBar=candlestickBars.get(i);
            cachLinkMap.put(candlestickBar.getOpenTime(), candlestickBar);
        }
        return size;
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
        CandlesticksCache.getInstance().cache();
    }

}
