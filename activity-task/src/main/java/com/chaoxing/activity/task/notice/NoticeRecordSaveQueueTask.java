package com.chaoxing.activity.task.notice;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.queue.notice.NoticeRecordSaveQueue;
import com.chaoxing.activity.service.queue.notice.handler.NoticeRecordSaveQueueHandleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**通知记录新增队列任务
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordSaveQueueTask
 * @description
 * @blame wwb
 * @date 2022-02-11 17:30:16
 */
@Slf4j
@Component
public class NoticeRecordSaveQueueTask {

	@Resource
	private NoticeRecordSaveQueue noticeRecordSaveQueue;
	@Resource
	private NoticeRecordSaveQueueHandleService noticeRecordSaveQueueHandleService;

	@Scheduled(fixedDelay = 10L)
	public void handle() throws InterruptedException {
		log.info("处理通知记录新增队列任务 start");
		NoticeRecordSaveQueue.QueueParamDTO queueParamDto = noticeRecordSaveQueue.pop();
		if (queueParamDto == null) {
			return;
		}
		try {
			log.info("根据参数:{} 处理通知记录新增队列任务", JSON.toJSON(queueParamDto));
			noticeRecordSaveQueueHandleService.handle(queueParamDto);
			log.info("处理通知记录新增队列任务 success");
		} catch (Exception e) {
			log.error("根据参数:{} 处理通知记录新增队列任务 error:{}", JSON.toJSON(queueParamDto), e.getMessage());
			e.printStackTrace();
			noticeRecordSaveQueue.delayPush(queueParamDto);
		} finally {
			log.info("处理活动考核结果计算任务 end");
		}

	}

}
