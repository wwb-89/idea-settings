package com.chaoxing.activity.service;

import com.chaoxing.activity.WfwClassDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.dto.manager.WfwRoleDTO;
import com.chaoxing.activity.model.User;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.enums.WfwRoleEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
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
	private UcApiService ucApiService;

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
			List<WfwClassDTO> manageClasses;
			List<WfwRoleDTO> roles;
			WfwClassDTO clazz = null;
			UserExtraInfoDTO userExtraInfo = ucApiService.getUserExtraInfoByFidAndUid(fid, uid);
			if (userExtraInfo == null) {
				roles = new ArrayList(){{
					add(WfwRoleDTO.builder()
							.id(WfwRoleEnum.STUDENT.getValue())
							.name(WfwRoleEnum.STUDENT.getName())
							.build());
				}};
				manageClasses = new ArrayList<>();
			} else {
				roles = userExtraInfo.getRoles();
				roles = Optional.ofNullable(roles).orElse(new ArrayList<>());
				if (CollectionUtils.isEmpty(roles)) {
					roles.add(WfwRoleDTO.builder()
							.id(WfwRoleEnum.STUDENT.getValue())
							.name(WfwRoleEnum.STUDENT.getName())
							.build());
				}

				clazz = WfwClassDTO.builder()
						.id(userExtraInfo.getClassId())
						.name(userExtraInfo.getClassName())
						.gradeId(userExtraInfo.getGradeId())
						.gradeName(userExtraInfo.getGradeName())
						.build();

				manageClasses = userExtraInfo.getClasses();
				manageClasses = Optional.ofNullable(manageClasses).orElse(new ArrayList<>());
			}

			loginUser.setUid(uid);
			loginUser.setFid(fid);
			loginUser.setRealName(passportUser.getRealName());
			String orgName = passportApiService.getOrgName(fid);
			loginUser.setOrgName(orgName);

			loginUser.setRoles(roles);
			loginUser.setClazz(clazz);
			loginUser.setManageClazzes(manageClasses);

			// 设置登录用户的角色
			loginUser.setStudent(false);
			loginUser.setTeacher(false);
			loginUser.setManager(ucApiService.isManager(fid, uid));
			// 设置角色
			for (WfwRoleDTO role : roles) {
				if (WfwRoleEnum.STUDENT.getValue().equals(role.getId())) {
					loginUser.setStudent(true);
				}
				if (WfwRoleEnum.TEACHER.getValue().equals(role.getId())) {
					loginUser.setTeacher(true);
				}
				if (WfwRoleEnum.MANAGER.getValue().equals(role.getId())) {
					loginUser.setManager(true);
				}
			}
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
		LoginUserDTO loginUser = valueOperations.get(getLoginUserCacheKey(uid, fid));
		return loginUser;
	}

	private String getLockKey(Integer uid, Integer fid) {
		StringBuilder lockKeyStringBuilder = new StringBuilder();
		lockKeyStringBuilder.append(CacheConstant.LOCK_CACHE_KEY_PREFIX);
		lockKeyStringBuilder.append("login");
		lockKeyStringBuilder.append(CacheConstant.LOCK_CACHE_KEY_PREFIX);
		lockKeyStringBuilder.append(uid);
		lockKeyStringBuilder.append(CacheConstant.LOCK_CACHE_KEY_PREFIX);
		lockKeyStringBuilder.append(fid);
		return lockKeyStringBuilder.toString();
	}

	private String getLoginUserCacheKey(Integer uid, Integer fid) {
		StringBuilder loginUserCacheKey = new StringBuilder();
		loginUserCacheKey.append(CacheConstant.CACHE_KEY_PREFIX);
		loginUserCacheKey.append("login");
		loginUserCacheKey.append(CacheConstant.CACHE_KEY_PREFIX);
		loginUserCacheKey.append(uid);
		loginUserCacheKey.append(CacheConstant.CACHE_KEY_PREFIX);
		loginUserCacheKey.append(fid);
		return loginUserCacheKey.toString();
	}

}
