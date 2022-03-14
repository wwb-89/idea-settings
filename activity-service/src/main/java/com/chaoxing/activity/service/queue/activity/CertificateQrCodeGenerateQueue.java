package com.chaoxing.activity.service.queue.activity;

import com.chaoxing.activity.service.queue.IQueue;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**证书二维码生成队列
 * @author wwb
 * @version ver 1.0
 * @className CertificateQrCodeGenerateQueue
 * @description
 * @blame wwb
 * @date 2022-03-14 10:26:13
 */
@Slf4j
@Service
public class CertificateQrCodeGenerateQueue implements IQueue<CertificateQrCodeGenerateQueue.QueueParamDTO> {

	private static final String CACHE_KEY = CacheConstant.QUEUE_CACHE_KEY_PREFIX + "certificate" + CacheConstant.CACHE_KEY_SEPARATOR + "qr_code_generate";

	@Resource
	private RedissonClient redissonClient;

	public void push(QueueParamDTO queueParam) {
		push(redissonClient, CACHE_KEY, queueParam);
	}

	public void delayPush(QueueParamDTO queueParam) {
		delayPush(redissonClient, CACHE_KEY, queueParam);
	}

	public QueueParamDTO pop() throws InterruptedException {
		return pop(redissonClient, CACHE_KEY);
	}

	@Data
	@Builder
	@AllArgsConstructor
	public static class QueueParamDTO {

		/** 证书id */
		private Integer certificateId;
		/** 次数 */
		private Integer times;

		public QueueParamDTO(Integer certificateId) {
			this.certificateId = certificateId;
			this.times = 0;
		}

		public void error() {
			this.times++;
		}

		public boolean canRepeatExec() {
			return times < CommonConstant.MAX_ERROR_TIMES;
		}

	}

}