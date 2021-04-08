package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**用户评价队列服务
 * @author wwb
 * @version ver 1.0
 * @className UserRatingQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 17:53:54
 */
@Slf4j
@Service
public class UserRatingQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "user_rating";
	private static final String VALUE_TYPE_SEPARATOR = "#";

	@Resource
	private RedisTemplate redisTemplate;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:55:25
	 * @param uid
	 * @param signId
	 * @return void
	*/
	public void add(Integer uid, Integer signId) {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(QUEUE_CACHE_KEY, generateValue(signId, uid));
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:56:23
	 * @param 
	 * @return java.lang.String
	*/
	public String get() {
		ListOperations<String, String> listOperations = redisTemplate.opsForList();
		String value = listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
		return value;
	}

	/**生成值
	 * @Description
	 * @author wwb
	 * @Date 2021-03-24 14:01:58
	 * @param signId
	 * @param uid
	 * @return java.lang.String
	 */
	private String generateValue(Integer signId, Integer uid) {
		return signId + VALUE_TYPE_SEPARATOR + uid;
	}

	/**从值中获取报名签到id
	 * @Description
	 * @author wwb
	 * @Date 2021-03-24 14:01:40
	 * @param value
	 * @return java.lang.Integer
	 */
	public Integer getSignIdFromValue(String value) {
		return resolveValue(value, 0);
	}

	/**从值中获取uid
	 * @Description
	 * @author wwb
	 * @Date 2021-03-24 14:01:34
	 * @param value
	 * @return java.lang.Integer
	 */
	public Integer getUidFromValue(String value) {
		return resolveValue(value, 1);
	}

	/**从值中获取指定的值
	 * @Description
	 * @author wwb
	 * @Date 2021-03-24 14:01:18
	 * @param value
	 * @param index`
	 * @return java.lang.Integer
	 */
	private Integer resolveValue(String value, Integer index) {
		if (StringUtils.isBlank(value) || !value.contains(VALUE_TYPE_SEPARATOR)) {
			return null;
		}
		String[] split = value.split(VALUE_TYPE_SEPARATOR);
		return Integer.parseInt(split[index]);
	}

}