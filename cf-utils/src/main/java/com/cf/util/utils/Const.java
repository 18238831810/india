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

	public final static String CASHIN_TAG = "cashin";




}
 