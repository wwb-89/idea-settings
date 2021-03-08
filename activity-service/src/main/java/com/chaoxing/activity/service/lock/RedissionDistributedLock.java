package com.chaoxing.activity.service.lock;

import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**基于redission的分布式锁服务
 * @author wwb
 * @version ver 1.0
 * @className RedissionDistributedLock
 * @description
 * @blame wwb
 * @date 2021-01-26 16:05:23
 */
@Slf4j
@Service
public class RedissionDistributedLock implements DistributedLock {

	@Resource
	private RedissonClient redissonClient;

	@Override
	public <T> T lock(String lockKey, long waitTime, Supplier<T> success, Consumer<Exception> fail) {
		RLock lock = redissonClient.getLock(lockKey);
		boolean tryLock = false;
		try {
			tryLock = lock.tryLock(waitTime, TimeUnit.SECONDS);
			if (tryLock) {
				return success.get();
			} else {
				fail.accept(new BusinessException("加锁失败"));
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
			fail.accept(e);
		} finally {
			if (tryLock) {
				lock.unlock();
			}
		}
		return null;
	}

	@Override
	public <T> T lock(String lockKey, Supplier<T> success, Consumer<Exception> fail) {
		return lock(lockKey, CacheConstant.LOCK_MAXIMUM_WAIT_TIME, success, fail);
	}

}