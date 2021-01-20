package com.chaoxing.activity.service;

import com.chaoxing.activity.dto.manager.WfwClassDTO;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.dto.manager.WfwRoleDTO;
import com.chaoxing.activity.model.User;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.manager.UcApiService;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.enums.WfwRoleEnum;
import com.google.common.collect.Lists;
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
				roles = Lists.newArrayList(WfwRoleDTO.builder()
						.id(WfwRoleEnum.STUDENT.getValue())
						.name(WfwRoleEnum.STUDENT.getName())
						.build());
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

			// 计算用户角色
			calUserRole(loginUser, roles);

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

	/**计算用户角色
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-20 10:18:09
	 * @param loginUser
	 * @param roles
	 * @return void
	*/
	private void calUserRole(LoginUserDTO loginUser, List<WfwRoleDTO> roles) {
		loginUser.setStudent(false);
		loginUser.setTeacher(false);
		loginUser.setManager(false);
		if (CollectionUtils.isNotEmpty(roles)) {
			// 设置角色
			for (WfwRoleDTO role : roles) {
				if (WfwRoleEnum.STUDENT.getValue().equals(role.getId())) {
					loginUser.setStudent(true);
				}
				if (WfwRoleEnum.TEACHER.getValue().equals(role.getId())) {
					loginUser.setTeacher(true);
				}
			}
		}
		loginUser.setManager(ucApiService.isManager(loginUser.getFid(), loginUser.getUid()));
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
