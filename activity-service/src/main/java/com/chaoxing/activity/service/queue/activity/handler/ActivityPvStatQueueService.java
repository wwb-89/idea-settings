package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.queue.activity.ActivityPvStatQueue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**活动pv统计队列服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityPvStatQueueService
 * @description
 * @blame wwb
 * @date 2022-01-19 15:47:35
 */
@Slf4j
@Service
public class ActivityPvStatQueueService {

	@Resource
	private MhApiService mhApiService;
	@Resource
	private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;

	public void handle(ActivityPvStatQueue.QueueParamDTO queueParam) {
		if (queueParam == null) {
			return;
		}
		Integer websiteId = queueParam.getWebsiteId();
		if (websiteId == null) {
			return;
		}
		Integer pv = mhApiService.countWebsitePv(websiteId);
		pv = Optional.ofNullable(pv).orElse(0);
		activityStatSummaryHandlerService.updatePv(queueParam.getActivityId(), pv);
	}

}