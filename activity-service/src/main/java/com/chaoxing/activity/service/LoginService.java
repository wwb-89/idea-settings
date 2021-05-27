package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.mapper.LoginCustomMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.LoginCustom;
import com.chaoxing.activity.model.User;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.user.UserService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**登录服务
 * @author wwb
 * @version ver 1.0
 * @className LoginService
 * @description
 * @blame wwb
 * @date 2020-11-12 17:38:38
 */
@Slf4j
@Service
public class LoginService {

	@Resource
	private UserService userService;
	@Resource
	private PassportApiService passportApiService;
	@Resource
	private LoginCustomMapper loginCustomMapper;

	@Resource
	private RedissonClient redissonClient;
	@Resource
	private RedisTemplate redisTemplate;

	/**登录
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 18:03:18
	 * @param uid
	 * @param fid
	 * @return com.chaoxing.activity.dto.LoginUserDTO
	*/
	public LoginUserDTO login(Integer uid, Integer fid) {
		RLock lock = redissonClient.getLock(getLockKey(uid, fid));
		boolean locked = false;
		try {
			locked = lock.tryLock(30, TimeUnit.SECONDS);
			LoginUserDTO loginUser = getLoginUser(uid, fid);
			if (loginUser != null) {
				return loginUser;
			}
			loginUser = new LoginUserDTO();
			// 获取用户信息
			PassportUserDTO passportUser = passportApiService.getByUid(uid);
			loginUser.setLoginName(passportUser.getLoginName());
			loginUser.setMobile(passportUser.getMobile());
			loginUser.setAffiliations(passportUser.getAffiliations());

			loginUser.setUid(uid);
			loginUser.setFid(fid);
			loginUser.setRealName(passportUser.getRealName());
			String orgName = passportApiService.getOrgName(fid);
			loginUser.setOrgName(orgName);

			cacheLoginUser(loginUser);
			userService.add(User.builder()
					.uid(loginUser.getUid())
					.realName(loginUser.getRealName())
					.loginName(loginUser.getLoginName())
					.mobile(loginUser.getMobile())
			.build());
			return loginUser;
		} catch (InterruptedException e) {
			log.error("uid:{}, fid:{}登录失败:{}", uid, fid, e.getMessage());
		} finally {
			if (locked) {
				lock.unlock();
			}
		}
		return null;
	}

	private void cacheLoginUser(LoginUserDTO loginUser) {
		Integer uid = loginUser.getUid();
		Integer fid = loginUser.getFid();
		ValueOperations<String, LoginUserDTO> valueOperations = redisTemplate.opsForValue();
		valueOperations.set(getLoginUserCacheKey(uid, fid), loginUser, 30, TimeUnit.SECONDS);
	}

	private LoginUserDTO getLoginUser(Integer uid, Integer fid) {
		ValueOperations<String, LoginUserDTO> valueOperations = redisTemplate.opsForValue();
		return valueOperations.get(getLoginUserCacheKey(uid, fid));
	}

	/**根据活动获取定制的登录数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-21 14:57:49
	 * @param activity
	 * @return java.lang.String
	*/
	public LoginCustom getLoginCustom(Activity activity) {
		if (activity == null) {
			return null;
		}
		String createAreaCode = activity.getCreateAreaCode();
		Integer createFid = activity.getCreateFid();
		// 查询定制的登录地址
		List<LoginCustom> loginCustoms = loginCustomMapper.selectList(new QueryWrapper<LoginCustom>()
				.lambda()
				.eq(LoginCustom::getDeleted, Boolean.FALSE)
				.and(wrapper -> wrapper.eq(LoginCustom::getAreaCode, createAreaCode).or().eq(LoginCustom::getFid, createFid))
		);
		if (CollectionUtils.isNotEmpty(loginCustoms)) {
			LoginCustom fidCustomLogin = null;
			LoginCustom areaCodeCustomLogin = null;
			for (LoginCustom loginCustom : loginCustoms) {
				String areaCode = loginCustom.getAreaCode();
				if (StringUtils.isNotBlank(areaCode)) {
					fidCustomLogin = loginCustom;
				}
				if (loginCustom.getFid() != null) {
					areaCodeCustomLogin = loginCustom;
				}
			}
			if (fidCustomLogin != null) {
				return fidCustomLogin;
			}
			if (areaCodeCustomLogin != null) {
				return areaCodeCustomLogin;
			}
		}
		return null;
	}

	private String getLockKey(Integer uid, Integer fid) {
		return CacheConstant.LOCK_CACHE_KEY_PREFIX +
				"login" +
				CacheConstant.LOCK_CACHE_KEY_PREFIX +
				uid +
				CacheConstant.LOCK_CACHE_KEY_PREFIX +
				fid;
	}

	private String getLoginUserCacheKey(Integer uid, Integer fid) {
		return CacheConstant.CACHE_KEY_PREFIX +
				"login" +
				CacheConstant.CACHE_KEY_PREFIX +
				uid +
				CacheConstant.CACHE_KEY_PREFIX +
				fid;
	}

}
