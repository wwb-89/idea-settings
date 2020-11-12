package com.chaoxing.activity.util.constant;

/**
 * @author wwb
 * @version ver 1.0
 * @className CacheConstant
 * @description
 * @blame wwb
 * @date 2020-11-09 11:19:51
 */
public class CacheConstant {

	private CacheConstant() {

	}

	public static final String CACHE_KEY_SEPARATOR = ":";
	/** 缓存key的前缀 */
	public static final String CACHE_KEY_PREFIX = "activity_engine" + CACHE_KEY_SEPARATOR;
	/** 锁缓存key的前缀 */
	public static final String LOCK_CACHE_KEY_PREFIX = "activity_engine" + CACHE_KEY_SEPARATOR + "lock" + CACHE_KEY_SEPARATOR;

}