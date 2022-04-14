package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.queue.activity.ActivityPvStatQueue;
import com.chaoxing.activity.service.queue.activity.WfwFormActivityDataUpdateQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
	private ActivityQueryService activityQueryService;
	@Resource
	private MhApiService mhApiService;
	@Resource
	private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;
	@Resource
	private WfwFormActivityDataUpdateQueue wfwFormActivityDataUpdateQueue;


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
		Integer activityId = queueParam.getActivityId();
		activityStatSummaryHandlerService.updatePv(activityId, pv);
		// pv更改触发活动信息更新（万能表单创建的活动）
		Activity activity = activityQueryService.getById(activityId);
		if (activity == null) {
			return;
		}
		// 万能表单活动数据更新
		if (activity.isWfwFormActivity()) {
			Integer formId = Optional.ofNullable(activity.getOrigin()).filter(StringUtils::isNotBlank).map(Integer::parseInt).orElse(null);
			Integer formUserId = activity.getOriginFormUserId();
			if (formId != null && formUserId != null) {
				wfwFormActivityDataUpdateQueue.push(WfwFormActivityDataUpdateQueue.QueueParamDTO.builder()
						.activityId(activityId)
						.fid(activity.getCreateFid())
						.formId(formId)
						.formUserId(activity.getOriginFormUserId())
						.build());
			}
		}
	}

}