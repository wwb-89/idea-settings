package com.chaoxing.activity.task.custom;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.queue.activity.ActivityPvStatQueue;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**湖北群艺馆活动pv统计任务
 * @author wwb
 * @version ver 1.0
 * @className HbqygActivityPvStatTask
 * @description
 * @blame wwb
 * @date 2022-01-19 15:42:34
 */
@Slf4j
@Component
public class HbqygActivityPvStatTask {

	@Resource
	private ActivityMapper activityMapper;
	@Resource
	private ActivityPvStatQueue activityPvStatQueue;

	private static final Integer MARKET_ID = 1413;

	@Scheduled(cron = "0 10 0 * * ?")
	public void handle() {
		List<Activity> activities = activityMapper.selectList(new LambdaQueryWrapper<Activity>()
				.eq(Activity::getMarketId, MARKET_ID)
				.ne(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
				.orderByDesc(Activity::getId)
				.last("LIMIT 1000")
		);
		if (CollectionUtils.isEmpty(activities)) {
			return;
		}
		for (Activity activity : activities) {
			ActivityPvStatQueue.QueueParamDTO queueParam = new ActivityPvStatQueue.QueueParamDTO(activity.getId(), activity.getWebsiteId());
			activityPvStatQueue.push(queueParam);
		}
	}

}