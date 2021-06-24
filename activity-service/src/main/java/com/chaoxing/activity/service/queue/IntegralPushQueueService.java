package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.dto.manager.IntegralPushDTO;
import com.chaoxing.activity.service.manager.IntegralApiService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**积分推送队列服务
 * @author wwb
 * @version ver 1.0
 * @className IntegralPushQueueService
 * @description 给第三方推送用户的积分
 * @blame wwb
 * @date 2021-03-26 15:18:06
 */
@Slf4j
@Service
public class IntegralPushQueueService implements IQueueService<IntegralPushDTO> {

	/** 队列缓存key */
	private static final String QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "integral_push";

	@Resource
	private IntegralApiService integralApiService;

	@Resource
	private RedissonClient redissonClient;

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
		push(redissonClient, QUEUE_CACHE_KEY, integralPush);
	}

	/**获取
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 15:38:08
	 * @param 
	 * @return com.chaoxing.activity.dto.manager.IntegralPushDTO
	*/
	public IntegralPushDTO get() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

}