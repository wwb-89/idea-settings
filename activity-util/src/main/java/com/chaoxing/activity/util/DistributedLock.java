package com.chaoxing.activity.util;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**分布式锁
 * @author wwb
 * @version ver 1.0
 * @className DistributedLock
 * @description
 * @blame wwb
 * @date 2021-01-26 16:01:18
 */
public interface DistributedLock {

	/**分布式锁方法
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-04 16:33:30
	 * @param lockKey 锁的键
	 * @param waitTime 等待锁时间
	 * @param success 成功执行的操作
	 * @param fail 失败执行的操作
	 * @return T
	*/
	<T> T lock(String lockKey, long waitTime, Supplier<T> success, Consumer<Exception> fail);

	/**分布式锁方法
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-04 16:33:50
	 * @param lockKey 锁的键
	 * @param success 锁成功后执行的操作
	 * @param fail 锁失败后执行的操作
	 * @return T
	*/
	<T> T lock(String lockKey, Supplier<T> success, Consumer<Exception> fail);

}