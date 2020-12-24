package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.manager.IntegralPushDTO;
import com.chaoxing.activity.mapper.IntegralPushScopeMapper;
import com.chaoxing.activity.model.IntegralPushScope;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.framework.AopContext;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**积分服务
 * @author wwb
 * @version ver 1.0
 * @className IntegralApiService
 * @description
 * @blame wwb
 * @date 2020-12-24 16:11:35
 */
@Slf4j
@Service
public class IntegralApiService {

	/** 积分回收地址 */
	private static final String INTEGRAL_PUSH_URL = "http://score.jxlll.chaoxing.com/services/ScoreService/addScore?fid=%d&uid=%d&sType=%d&resourceId=%s&resourceName=%s";
	/** 积分回收队列缓存 */
	private static final String INTEGRAL_PUSH_QUEUE_CACHE_NAME = CacheConstant.CACHE_KEY_PREFIX + "integral_push_queue";
	/** 积分回收范围缓存 */
	private static final String INTEGRAL_PUSH_SCOPE_CACHE_NAME = CacheConstant.CACHE_KEY_PREFIX + "integral_push_scope";

	@Resource
	private IntegralPushScopeMapper integralPushScopeMapper;

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;
	@Resource
	private RedisTemplate redisTemplate;

	@Cacheable(value = INTEGRAL_PUSH_SCOPE_CACHE_NAME, unless = "#result == null")
	public List<Integer> listIntegralPushScope() {
		List<IntegralPushScope> integralPushScopes = integralPushScopeMapper.selectList(new QueryWrapper<>());
		if (CollectionUtils.isNotEmpty(integralPushScopes)) {
			return integralPushScopes.stream().map(IntegralPushScope::getFid).collect(Collectors.toList());
		} else {
			return null;
		}
	}
	/**积分推送
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 16:14:47
	 * @param integralPush
	 * @return void
	*/
	public void integralPush(IntegralPushDTO integralPush) {
		Integer fid = integralPush.getFid();
		List<Integer> fids = ((IntegralApiService) (AopContext.currentProxy())).listIntegralPushScope();
		if (!fids.contains(fid)) {
			return;
		}
		ListOperations<String, IntegralPushDTO> listOperations = redisTemplate.opsForList();
		listOperations.leftPush(INTEGRAL_PUSH_QUEUE_CACHE_NAME, integralPush);
	}

	/**处理任务
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 16:30:54
	 * @param 
	 * @return void
	*/
	public void handleTask() {
		ListOperations<String, IntegralPushDTO> listOperations = redisTemplate.opsForList();
		// 查询现在有多少条数据
		Long longSize = listOperations.size(INTEGRAL_PUSH_QUEUE_CACHE_NAME);
		long size = Optional.ofNullable(longSize).orElse(0L).longValue();
		if (size < 1) {
			return;
		}
		for (int i = 0; i < size; i++) {
			IntegralPushDTO integralPushDTO = listOperations.rightPop(INTEGRAL_PUSH_QUEUE_CACHE_NAME);
			try {
				pushIntegral(integralPushDTO);
			} catch (Exception e) {
				e.printStackTrace();
				listOperations.leftPush(INTEGRAL_PUSH_QUEUE_CACHE_NAME, integralPushDTO);
			}
		}

	}

	/**推送积分数据
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 16:54:48
	 * @param integralPush
	 * @return void
	*/
	private void pushIntegral(IntegralPushDTO integralPush) {
		Integer uid = integralPush.getUid();
		Integer fid = integralPush.getFid();
		Integer type = integralPush.getType();
		String resourceId = Optional.ofNullable(integralPush.getResourceId()).filter(StringUtils::isNotBlank).orElse("");
		String resourceName = Optional.ofNullable(integralPush.getResourceName()).filter(StringUtils::isNotBlank).orElse("");
		String url = String.format(INTEGRAL_PUSH_URL, fid, uid, type, resourceId, resourceName);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
	}

}