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
	/** 锁最大等待时间 */
	public static final long LOCK_MAXIMUM_WAIT_TIME = 5L;
	/** 队列缓存前缀 */
	public static final String QUEUE_CACHE_KEY_PREFIX = CACHE_KEY_PREFIX + "queue" + CACHE_KEY_SEPARATOR;
	/** 特殊用户管理活动的key前缀 */
	public static final String ACTIVITY_USER_OPERATE_KEY_PREFIX = CACHE_KEY_PREFIX + "activity_special_user" + CACHE_KEY_SEPARATOR;


	public static String buildSpecialUserKey(Integer activityId, Integer uid) {
		return ACTIVITY_USER_OPERATE_KEY_PREFIX + activityId + CACHE_KEY_SEPARATOR + uid;
	}

}