package com.binance.api.client.constant;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandlestickDto implements Serializable {
    private Long openTime;
    private String open;
    private String high;
    private String low;
    private String close;
    private String volume;
    private Long closeTime;
    private String quoteAssetVolume;
    private Long numberOfTrades;
    private String takerBuyBaseAssetVolume;
    private String takerBuyQuoteAssetVolume;
}
