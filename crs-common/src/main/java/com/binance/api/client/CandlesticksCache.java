package com.binance.api.client;

import com.binance.api.client.constant.CandlestickDto;
import com.binance.api.client.domain.market.Candlestick;
import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Array;
import java.util.*;

/**
 * Illustrates how to use the klines/candlesticks event stream to create a local cache of bids/asks for a symbol.
 */

public class CandlesticksCache {
    /**
     * Key is the start/open time of the candle, and the value contains candlestick date.
     */

    private static CandlesticksCache candlesticksCache = null;
    private static final int maximumSize = 240;
    private static final  String defaulSymbol="btcusdt";
    /**
     * 限制Map大小为maximumSize
     */
    private static LinkedHashMap<Long, CandlestickDto> cachLinkMap = new LinkedHashMap<Long, CandlestickDto>() {

        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, CandlestickDto> eldest) {
            return size() > maximumSize;
        }
    };

    private CandlesticksCache() {
    }

    public static CandlesticksCache getInstance() {
        if (candlesticksCache == null) candlesticksCache = new CandlesticksCache();
        return candlesticksCache;
    }




    private CandlestickDto toCandlestickDto(Candlestick candlestick )
    {
           return CandlestickDto.builder().close(candlestick.getClose())
                    .closeTime(candlestick.getCloseTime())
                    .high(candlestick.getHigh())
                    .low(candlestick.getLow())
                    .numberOfTrades(candlestick.getNumberOfTrades())
                    .open(candlestick.getOpen())
                    .openTime(candlestick.getOpenTime())
                    .quoteAssetVolume(candlestick.getQuoteAssetVolume())
                    .takerBuyBaseAssetVolume(candlestick.getTakerBuyBaseAssetVolume())
                    .takerBuyQuoteAssetVolume(candlestick.getTakerBuyQuoteAssetVolume())
                    .volume(candlestick.getVolume()).build();
    }

    private List<CandlestickDto> getCandlestickDtoFromBinance(String symbol, CandlestickInterval interval,Integer size) {
        BinanceApiClientFactory factory = BinanceApiClientFactory.newInstance();
        BinanceApiRestClient client = factory.newRestClient();
        List<Candlestick> list = client.getCandlestickBars(symbol.toUpperCase(), interval);
        if(size==null) return null;
        List<CandlestickDto> result = new ArrayList<>();
        int start =list.size()-size;
        start=start<=0?0:start;
        for (int i=start;i< list.size();i++) {
            CandlestickDto candlestickDto = toCandlestickDto(list.get(i));
            result.add(candlestickDto);
        }
        return result;
    }


    public Collection<CandlestickDto> getCandlestickDto(String symbol, CandlestickInterval interval, Integer size) {
        if(size==null || size==0) return null;
        if(defaulSymbol.equalsIgnoreCase(symbol) && "1m".equalsIgnoreCase(interval.getIntervalId()))
        {
            Collection<CandlestickDto> collection= getBianaceBTCCandlesticksCache().values();
            if(size>=collection.size()) return collection;
            else {
               return getCandlestickDtoLimit(collection,size);
            }
        }
        return   getCandlestickDtoFromBinance( symbol,  interval, size);
    }

    private List<CandlestickDto> getCandlestickDtoLimit(Collection<CandlestickDto> collection,int size)
    {
        List<CandlestickDto> list =  new ArrayList<>();
        Iterator<CandlestickDto> iterator= collection.iterator();
        int index=0;
        while (iterator.hasNext())
        {
            list.add(iterator.next());
            index++;
            if(index>=size) break;
        }
        return list;
    }

    public Collection<CandlestickDto> getCandlestickDto(String symbol, String interval, Integer size) {
        return getCandlestickDto( symbol,  getInterval(interval), size);
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
     * @return a klines/candlestick cache, containing the open/start time of the candlestick as the key,
     * and the candlestick data as the value.
     */
    public Map<Long, CandlestickDto> getBianaceBTCCandlesticksCache() {
        if(cachLinkMap.isEmpty())
        {
           return cacheBtcOneMinu();
        }
        else return cachLinkMap;
    }

    public synchronized Map<Long, CandlestickDto> cacheBtcOneMinu() {
        List<CandlestickDto> list=   getCandlestickDtoFromBinance(defaulSymbol, CandlestickInterval.ONE_MINUTE,maximumSize);
        for (CandlestickDto candlestickDto:list) {
            cachLinkMap.put(candlestickDto.getOpenTime(),candlestickDto);
        }
        return cachLinkMap;
    }

    public static void main(String[] args) {
        CandlesticksCache.getInstance().getBianaceBTCCandlesticksCache();
    }

}
