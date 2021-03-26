package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.dto.manager.IntegralPushDTO;
import com.chaoxing.activity.service.manager.IntegralApiService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**积分推送队列服务
 * @author wwb
 * @version ver 1.0
 * @className IntegralPushQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 15:18:06
 */
@Slf4j
@Service
public class IntegralPushQueueService {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "integral_push";

	@Resource
	private IntegralApiService integralApiService;

	@Resource
	private RedisTemplate redisTemplate;

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:38:01
	 * @param integralPush
	 * @return void
	*/
	public void add(@NotNull IntegralPushDTO integralPush) {
		Integer fid = integralPush.getFid();
		List<Integer> fids = integralApiService.listIntegralPushScope();
		if (!fids.contains(fid)) {
			return;
		}
		ListOperations<String, IntegralPushDTO> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(QUEUE_CACHE_KEY, integralPush);
	}

	/**获取
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:38:08
	 * @param 
	 * @return com.chaoxing.activity.dto.manager.IntegralPushDTO
	*/
	public IntegralPushDTO get() {
		ListOperations<String, IntegralPushDTO> listOperations = redisTemplate.opsForList();
		return listOperations.rightPop(QUEUE_CACHE_KEY, CommonConstant.QUEUE_GET_WAIT_TIME);
	}

}