package com.chaoxing.activity.service.queue;

import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**导出记录下载地址队列服务
 * @author wwb
 * @version ver 1.0
 * @className ExportRecordDownloadUrlQueueService
 * @description
 * @blame wwb
 * @date 2021-06-03 15:11:01
 */
@Slf4j
@Service
public class ExportRecordDownloadUrlQueueService implements IQueueService<Integer> {

	/** 队列key */
	private static final String EXPORT_RECORD_CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "export_record_download_url";

	@Resource
	private RedissonClient redissonClient;

	public void push(Integer taskId) {
		push(redissonClient, EXPORT_RECORD_CACHE_KEY, taskId);
	}

	public Integer pop() throws InterruptedException {
		return pop(redissonClient, EXPORT_RECORD_CACHE_KEY);
	}

}
