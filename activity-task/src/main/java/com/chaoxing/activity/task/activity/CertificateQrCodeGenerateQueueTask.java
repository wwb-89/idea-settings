package com.chaoxing.activity.task.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.activity.CertificateQrCodeGenerateQueue;
import com.chaoxing.activity.service.queue.activity.handler.CertificateQrCodeGenerateQueueService;
import com.chaoxing.activity.util.PathUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**证书二维码生成队列任务
 * @author wwb
 * @version ver 1.0
 * @className CertificateQrCodeGenerateQueueTask
 * @description
 * @blame wwb
 * @date 2022-03-14 11:28:09
 */
@Slf4j
@Component
public class CertificateQrCodeGenerateQueueTask {

	@Resource
	private CertificateQrCodeGenerateQueue certificateQrCodeGenerateQueue;
	@Resource
	private CertificateQrCodeGenerateQueueService certificateQrCodeGenerateQueueService;

	@Scheduled(fixedDelay = 10L)
	public void handle() throws InterruptedException {
		log.info("处理证书二维码生成队列任务 start");
		CertificateQrCodeGenerateQueue.QueueParamDTO queueParam = certificateQrCodeGenerateQueue.pop();
		if (queueParam == null) {
			log.info("处理证书二维码生成队列任务 忽略");
			return;
		}
		try {
			log.info("根据参数:{} 处理证书二维码生成队列任务 start", JSON.toJSONString(queueParam));
			String rootPath = PathUtils.getUploadRootPath();
			certificateQrCodeGenerateQueueService.handle(queueParam, rootPath);
			log.info("根据参数:{} 处理证书二维码生成队列任务 success", JSON.toJSONString(queueParam));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("根据参数:{} 处理证书二维码生成队列任务 error:{}", JSON.toJSONString(queueParam), e.getMessage());
			if (queueParam.canRepeatExec()) {
				certificateQrCodeGenerateQueue.delayPush(queueParam);
			}
		}

	}

}