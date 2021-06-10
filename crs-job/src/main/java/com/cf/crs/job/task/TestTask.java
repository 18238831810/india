/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.job.task;

import com.binance.api.client.CandlesticksCache;
import com.binance.api.client.domain.market.CandlestickInterval;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务(演示Demo，可删除)
 *
 * testTask为spring bean的名称
 *
 * @author Mark sunlightcs@gmail.com
 */
@Slf4j
@Component("task")
public class TestTask implements ITask{

	@Override
	public void run(String params){
		try {
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
		CandlesticksCache.getInstance().getBianaceBTCCandlesticksCache();
	}

}