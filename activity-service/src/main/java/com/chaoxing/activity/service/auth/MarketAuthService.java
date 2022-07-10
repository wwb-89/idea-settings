package com.chaoxing.activity.service.auth;

import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.google.common.collect.Maps;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**活动市场授权
 * @author wwb
 * @version ver 1.0
 * @className MarketAuthService
 * @description
 * @blame wwb
 * @date 2022-04-01 12:26:16
 */
@Service
public class MarketAuthService {

	@Resource
	private RedisTemplate redisTemplate;

	/** 特殊用户管理活动的key前缀 */
	public static final String ACTIVITY_USER_OPERATE_KEY_PREFIX = CacheConstant.CACHE_KEY_PREFIX + "market_special_user" + CacheConstant.CACHE_KEY_SEPARATOR;

	public static final String KEY = "owin12&djHStwqesdd";

	/**获取enc
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-03-03 18:44:31
	 * @param marketId
	 * @param uid
	 * @return
	 */
	public static String buildActivityAuthEnc(Integer marketId, Integer uid) {
		StringBuilder enc = new StringBuilder();
		String today = LocalDate.now().format(DateUtils.DAY_DATE_TIME_FORMATTER);
		Map<String, String> paramMap = Maps.newHashMap();
		paramMap.put("activityId", Optional.ofNullable(marketId).map(String::valueOf).orElse(""));
		paramMap.put("uid", Optional.ofNullable(uid).map(String::valueOf).orElse(""));
		paramMap.put("date", today);
		for (Map.Entry<String, String> entry : paramMap.entrySet()) {
			enc.append("[").append(entry.getKey()).append("=")
					.append(entry.getValue()).append("]");
		}
		return DigestUtils.md5Hex(enc + "[" + KEY + "]");
	}

	/**校验enc
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-03-03 18:45:41
	 * @param marketId
	 * @param uid
	 * @param enc
	 * @return
	 */
	private boolean validateEnc(Integer marketId, Integer uid, String enc) {
		return Objects.equals(buildActivityAuthEnc(marketId, uid), enc);
	}

	/**生成key
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-03-03 18:45:57
	 * @param marketId
	 * @param uid
	 * @return
	 */
	private String generateCacheKey(Integer marketId, Integer uid) {
		return ACTIVITY_USER_OPERATE_KEY_PREFIX + marketId + CacheConstant.CACHE_KEY_SEPARATOR + uid;
	}

	/**活动市场是否授权用户
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-03-03 18:30:31
	 * @param marketId
	 * @param uid
	 * @return
	 */
	public boolean isAuthorizedUser(Integer marketId, Integer uid) {
		return redisTemplate.hasKey(generateCacheKey(marketId, uid));
	}

	/**活动市场授权用户
	 * @Description
	 * @author huxiaolong
	 * @Date 2022-03-03 18:30:51
	 * @param marketId
	 * @param uid
	 * @return
	 */
	public void authorizedUser(Integer marketId, Integer uid, String enc) {
		// enc验证非法，返回
		if (!validateEnc(marketId, uid, enc)) {
			return;
		}
		authorizedUserIgnoreEnc(marketId, uid);
	}

	/**给用户授权（忽略enc）
	 * @Description
	 * @author wwb
	 * @Date 2022-04-01 12:21:41
	 * @param marketId
	 * @param uid
	 * @return void
	 */
	public void authorizedUserIgnoreEnc(Integer marketId, Integer uid) {
		if (uid == null) {
			return;
		}
		String cacheKey = generateCacheKey(marketId, uid);
		// 如果存在key，续期
		if (isAuthorizedUser(marketId, uid)) {
			redisTemplate.expire(cacheKey, 30, TimeUnit.MINUTES);
			return;
		}
		redisTemplate.opsForValue().set(cacheKey, 1, 30, TimeUnit.MINUTES);
	}

}