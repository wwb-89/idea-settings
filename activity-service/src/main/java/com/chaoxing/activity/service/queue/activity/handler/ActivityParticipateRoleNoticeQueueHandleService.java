package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.service.queue.activity.ActivityParticipateRoleNoticeQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**活动参与角色通知队列处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityParticipateRoleNoticeQueueHandleService
 * @description
 * @blame wwb
 * @date 2022-02-16 17:54:19
 */
@Slf4j
@Service
public class ActivityParticipateRoleNoticeQueueHandleService {

	public void handle(ActivityParticipateRoleNoticeQueue.QueueParamDTO queueParamDto) {
		// 调用通知的接口
	}

}