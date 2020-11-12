package com.chaoxing.activity.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.UserMapper;
import com.chaoxing.activity.model.User;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserService
 * @description
 * @blame wwb
 * @date 2020-11-12 17:49:26
 */
@Slf4j
@Service
public class UserService {

	@Resource
	private UserMapper userMapper;

	@Resource
	private RedissonClient redissonClient;

	/**根据uid查询用户
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 17:48:35
	 * @param uid
	 * @return com.chaoxing.activity.model.User
	 */
	public User getByUid(Integer uid) {
		return userMapper.selectOne(new QueryWrapper<User>()
				.lambda()
				.eq(User::getUid, uid)
		);
	}

	/**新增用户
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 17:50:46
	 * @param user
	 * @return void
	*/
	public void add(User user) {
		String lockKey = getLockKey(user.getUid());
		RLock lock = redissonClient.getLock(lockKey);
		boolean locked = false;
		try {
			locked = lock.tryLock(30, TimeUnit.SECONDS);
			User existUser = getByUid(user.getUid());
			if (existUser == null) {
				userMapper.insert(user);
			}
		} catch (InterruptedException e) {
			log.error("新增用户:{}失败:{}", JSON.toJSONString(user), e.getMessage());
		}finally {
			if (locked) {
				lock.unlock();
			}
		}
	}

	private String getLockKey(Integer uid) {
		StringBuilder lockKeyStringBuilder = new StringBuilder();
		lockKeyStringBuilder.append(CacheConstant.LOCK_CACHE_KEY_PREFIX);
		lockKeyStringBuilder.append("add");
		lockKeyStringBuilder.append(CacheConstant.CACHE_KEY_SEPARATOR);
		lockKeyStringBuilder.append("user");
		lockKeyStringBuilder.append(CacheConstant.CACHE_KEY_SEPARATOR);
		lockKeyStringBuilder.append(uid);
		return lockKeyStringBuilder.toString();
	}

}