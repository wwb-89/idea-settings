package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.service.manager.IntegralApiService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**积分推送队列服务
 * @author wwb
 * @version ver 1.0
 * @className IntegralPushQueue
 * @description 给第三方推送用户的积分
 * @blame wwb
 * @date 2021-03-26 15:18:06
 */
@Slf4j
@Service
public class IntegralPushQueue implements IQueue<IntegralPushQueue.IntegralPushDTO> {

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
	public void push(@NotNull IntegralPushDTO integralPush) {
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
	public IntegralPushDTO pop() throws InterruptedException {
		return pop(redissonClient, QUEUE_CACHE_KEY);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IntegralPushDTO {

		/** 用户id */
		private Integer uid;
		/** 机构id */
		private Integer fid;
		/** 类型 */
		private Integer type;
		/** 资源id */
		private String resourceId;
		/** 资源名称 */
		private String resourceName;

	}

}