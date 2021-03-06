package com.cf.util.utils;

import org.omg.CORBA.PUBLIC_MEMBER;

/**
 * 常用变量
 * @author frank
 * @date 2019/08/23
 */
public class Const {

	public static  final  String PUBLIC ="/public/";
	public static  final  String PUBLIC_URL ="/public/**";

	public static  final  String API ="/api/";
	public static  final  String API_URL ="/api/**";

	public static  final  String ADMIN ="/admin/";
	public static  final  String ADMIN_URL ="/admin/**";

	public static  final String AUTHORIZATION = "Authorization";

	public static  final String AUTHORIZATION_PREFIX = "authorization_";

	public static  final String AGORA_TOKEN = "agora_token_";


	/**
	 * 用户token
	 */
	public static  final  String TOKEN ="token";

	/**
	 * 用户id
	 */
	public static  final  String UID ="uid";

	/**
	 * 多久之内可以下单
	 */
	public final static long LIMIT_TIME = 40;

	/**
	 * redis stream 消息队列topic
	 */
	public final static String REDIS_STREAM_TOPIC = "redis_stream_topic";

	/**
	 * redis stream 消息队列 存款 tag
	 */
	public final static String CASHIN_TAG = "redis_tag_cashin";

	/**
	 * redis stream 消息队列 交易 tag
	 */
	public final static String ORDER_TAG = "redis_tag_order";

	/**
	 * redis stream 消息队列 自定义交易数据 tag
	 */
	public final static String ORDER_SETTING_TAG = "redis_tag_setting_order";


	/**
	 * redis stream 消息队列 交易盈利 tag
	 */
	public final static String ORDER_PROFIT_TAG = "redis_tag_order_profit";




}
 