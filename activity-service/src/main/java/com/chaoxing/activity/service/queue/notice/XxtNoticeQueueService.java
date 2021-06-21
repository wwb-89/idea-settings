package com.chaoxing.activity.service.queue.notice;

import com.chaoxing.activity.dto.manager.NoticeDTO;
import com.chaoxing.activity.service.queue.IQueueService;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**学习通通知队列服务
 * @author wwb
 * @version ver 1.0
 * @className XxtNoticeQueueService
 * @description
 * @blame wwb
 * @date 2021-03-26 18:23:52
 */
@Slf4j
@Service
public class XxtNoticeQueueService implements IQueueService<NoticeDTO> {

	/** 通知队列缓存key */
	private static final String NOTICE_QUEUE_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice";

	@Resource
	private RedissonClient redissonClient;

	/**将通知加入队列中
	 * @Description
	 * @author wwb
	 * @Date 2021-03-25 18:08:03
	 * @param notice
	 * @return void
	 */
	public void add(NoticeDTO notice) {
		push(redissonClient, NOTICE_QUEUE_CACHE_KEY, notice);
	}

	/**从队列中获取需要处理的通知
	 * @Description
	 * @author wwb
	 * @Date 2021-03-25 18:10:19
	 * @param
	 * @return com.chaoxing.sign.dto.manager.NoticeDTO
	 */
	public NoticeDTO get() throws InterruptedException {
		return pop(redissonClient, NOTICE_QUEUE_CACHE_KEY);
	}

}