package com.chaoxing.activity.service.queue.notice;

import com.chaoxing.activity.model.NoticeRecord;
import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**通知记录保存队列
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordSaveQueue
 * @description
 * @blame wwb
 * @date 2022-02-11 17:11:57
 */
@Slf4j
@Service
public class NoticeRecordSaveQueue implements IQueue<NoticeRecordSaveQueue.QueueParamDTO> {

	private static final String KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "notice_record" + CacheConstant.CACHE_KEY_SEPARATOR + "save";

	@Resource
	private RedissonClient redissonClient;

	public void push(QueueParamDTO queueParam) {
		push(redissonClient, KEY, queueParam);
	}

	public void delayPush(QueueParamDTO queueParam) {
		delayPush(redissonClient, KEY, queueParam);
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, KEY);
	}

	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 活动id */
		private Integer activityId;
		/** 通知类型 */
		private NoticeRecord.TypeEnum type;
		/** 通知内容 */
		private String content;
		/** 时间 */
		private Long timestamp;

	}

}
