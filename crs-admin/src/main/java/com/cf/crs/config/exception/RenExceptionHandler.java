/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.config.exception;

import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.common.utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 异常处理器
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.0.0
 */
@Slf4j
@RestControllerAdvice
public class RenExceptionHandler {


	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RenException.class)
	public Result handleRenException(RenException ex){
		Result result = new Result();
		result.error(ex.getCode(), ex.getMsg());

		return result;
	}

	/**
	 * 索引
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	public Result handleDuplicateKeyException(DuplicateKeyException ex){
		Result result = new Result();
		result.error(ErrorCode.DB_RECORD_EXISTS);
		return result;
	}

	/**
	 * 全局异常
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public Result handleException(Exception ex){
		log.error(ex.getMessage(), ex);
		return new Result().error();
	}

}