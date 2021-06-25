package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.queue.activity.ActivityReleaseScopeChangeQueueService;
import com.chaoxing.activity.util.enums.ModuleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**活动发布范围改变任务
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseScopeChangeTask
 * @description
 * @blame wwb
 * @date 2021-03-26 17:28:51
 */
@Slf4j
@Component
public class ActivityReleaseScopeChangeTask {

	@Resource
	private ActivityReleaseScopeChangeQueueService activityReleaseScopeChangeQueueService;
	@Resource
	private ActivityModuleService activityModuleService;
	@Resource
	private WorkApiService workApiService;

	/**刷新范围缓存
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:30:06
	 * @param 
	 * @return void
	*/
	@Scheduled(fixedDelay = 1L)
	public void clearScopeCache() throws InterruptedException {
		Integer activityId = activityReleaseScopeChangeQueueService.pop();
		if (activityId == null) {
			return;
		}
		List<String> externalIds = activityModuleService.listExternalIdsByActivityIdAndType(activityId, ModuleTypeEnum.WORK.getValue());
		if (CollectionUtils.isNotEmpty(externalIds)) {
			try {
				workApiService.clearActivityParticipateScopeCache(externalIds.stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList()));
			} catch (Exception e) {
				e.printStackTrace();
				log.error("活动:{} 发布范围改变更新作品征集缓存的范围error:{}", activityId, e.getMessage());
				activityReleaseScopeChangeQueueService.push(activityId);
			}

		}
	}

}