package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.IntegralPushScopeMapper;
import com.chaoxing.activity.model.IntegralPushScope;
import com.chaoxing.activity.service.queue.IntegralPushQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
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
	private static final String INTEGRAL_PUSH_URL = DomainConstant.SCORE + "/services/ScoreService/addScore?fid=%d&uid=%d&sType=%d&resourceId=%s&resourceName=%s";

	/** 积分回收范围缓存 */
	private static final String INTEGRAL_PUSH_SCOPE_CACHE_NAME = CacheConstant.CACHE_KEY_PREFIX + "integral_push_scope";

	@Resource
	private IntegralPushScopeMapper integralPushScopeMapper;

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	@Cacheable(value = INTEGRAL_PUSH_SCOPE_CACHE_NAME, unless = "#result == null")
	public List<Integer> listIntegralPushScope() {
		List<IntegralPushScope> integralPushScopes = integralPushScopeMapper.selectList(new QueryWrapper<>());
		if (CollectionUtils.isNotEmpty(integralPushScopes)) {
			return integralPushScopes.stream().map(IntegralPushScope::getFid).collect(Collectors.toList());
		} else {
			return null;
		}
	}

	/**推送积分数据
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-24 16:54:48
	 * @param integralPush
	 * @return void
	*/
	public void pushIntegral(IntegralPushQueue.IntegralPushDTO integralPush) {
		Integer uid = integralPush.getUid();
		Integer fid = integralPush.getFid();
		Integer type = integralPush.getType();
		String resourceId = Optional.ofNullable(integralPush.getResourceId()).filter(StringUtils::isNotBlank).orElse("");
		String resourceName = Optional.ofNullable(integralPush.getResourceName()).filter(StringUtils::isNotBlank).orElse("");
		String url = String.format(INTEGRAL_PUSH_URL, fid, uid, type, resourceId, resourceName);
		String result = restTemplate.getForObject(url, String.class);
		log.info("推送积分:{}, 结果:{}", JSON.toJSONString(integralPush), result);
	}

}