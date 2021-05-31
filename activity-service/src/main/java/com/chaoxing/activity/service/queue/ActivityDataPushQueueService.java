package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动数据推送队列服务
 * @author wwb
 * @version ver 1.0
 * @className SecondClassroomActivityPushQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 16:58:43
 */
@Slf4j
@Service
public class ActivityDataPushQueueService implements IQueueService<Integer> {

	/** 新增队列缓存key */
	private static final String ADD_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_push" + CacheConstant.CACHE_KEY_SEPARATOR + "add";
	/** 修改队列缓存key */
	private static final String UPDATE_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_push" + CacheConstant.CACHE_KEY_SEPARATOR + "update";
	/** 修改队列缓存key */
	private static final String DELETE_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "activity_push" + CacheConstant.CACHE_KEY_SEPARATOR + "delete";

	@Resource
	private RedisTemplate redisTemplate;
	@Resource
	private RedissonClient redissonClient;
	@Resource
	private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:01:58
	 * @param activity
	 * @return void
	*/
	public void add(Activity activity) {
		if (configuredActivityDataRepo(activity)) {
			add(activity.getId());
		}
	}

	/**往队列中添加数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 17:48:46
	 * @param activityId
	 * @return void
	 */
	public void add(Integer activityId) {
		push(redissonClient, ADD_QUEUE_CACHE_KEY, activityId);
	}

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:30:08
	 * @param activity
	 * @return void
	*/
	public void update(Activity activity) {
		if (configuredActivityDataRepo(activity)) {
			update(activity.getId());
		}
	}

	/**往队列中添加数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:32:18
	 * @param activityId
	 * @return void
	*/
	public void update(Integer activityId) {
		push(redissonClient, UPDATE_QUEUE_CACHE_KEY, activityId);
	}

	/**往队列中添加数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 19:30:08
	 * @param activity
	 * @return void
	 */
	public void delete(Activity activity) {
		if (configuredActivityDataRepo(activity)) {
			delete(activity.getId());
		}
	}

	/**往队列中添加数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 19:32:18
	 * @param activityId
	 * @return void
	 */
	public void delete(Integer activityId) {
		push(redissonClient, DELETE_QUEUE_CACHE_KEY, activityId);
	}


	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:02:18
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer getAdd() throws InterruptedException {
		return pop(redissonClient, ADD_QUEUE_CACHE_KEY);
	}

	/**从队列中获取数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:31:01
	 * @param 
	 * @return java.lang.Integer
	*/
	public Integer getUpdate() throws InterruptedException {
		return pop(redissonClient, UPDATE_QUEUE_CACHE_KEY);
	}

	/**从队列中获取数据
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 19:31:01
	 * @param
	 * @return java.lang.Integer
	 */
	public Integer getDelete() throws InterruptedException {
		return pop(redissonClient, DELETE_QUEUE_CACHE_KEY);
	}

	/**配置了活动数据仓库
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-19 15:12:39
	 * @param activity
	 * @return boolean
	*/
	public boolean configuredActivityDataRepo(Activity activity) {
		List<OrgDataRepoConfigDetail> orgDataRepoConfigDetails = orgDataRepoConfigQueryService.listParticipateTimeConfigDetail(activity.getCreateFid(), OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY.getValue());
		return CollectionUtils.isNotEmpty(orgDataRepoConfigDetails);
	}

}