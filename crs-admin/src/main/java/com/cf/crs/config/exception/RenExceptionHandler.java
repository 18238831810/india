/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.crs.io
 *
 * 版权所有，侵权必究！
 */

package com.cf.crs.config.exception;

import com.cf.crs.common.constant.MsgError;
import com.cf.crs.common.exception.AuthException;
import com.cf.crs.common.exception.ErrorCode;
import com.cf.crs.common.exception.RenException;
import com.cf.crs.common.utils.Result;
import com.cf.util.http.HttpWebResult;
import com.cf.util.http.ResultJson;
import com.cf.util.utils.MessageUtil;
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
	 * 权限异常
	 */
	@ExceptionHandler(AuthException.class)
	public ResultJson handleRenException(AuthException ex){
		return HttpWebResult.getMonoError(ErrorCode.FORBIDDEN, MsgError.AUTH_FAIL);
	}


	/**
	 * 处理自定义异常
	 */
	@ExceptionHandler(RenException.class)
	public ResultJson handleRenException(RenException ex){
		String msg = ex.getMsg();
		if (msg.startsWith("msg_")) msg = MessageUtil.get(msg);
		return HttpWebResult.getMonoError(ex.getCode(),msg);
	}

	/**
	 * 索引
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(DuplicateKeyException.class)
	public ResultJson handleDuplicateKeyException(DuplicateKeyException ex){
		return HttpWebResult.getMonoError(ErrorCode.DB_RECORD_EXISTS,ex.getMessage());
	}

	/**
	 * 全局异常
	 * @param ex
	 * @return
	 */
	@ExceptionHandler(Exception.class)
	public ResultJson handleException(Exception ex){
		log.error(ex.getMessage(),ex);
		return HttpWebResult.getMonoError(ex.getMessage());
	}

}