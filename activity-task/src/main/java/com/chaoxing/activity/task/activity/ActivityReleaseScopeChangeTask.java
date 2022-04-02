package com.chaoxing.activity.task.activity;

import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.queue.activity.ActivityReleaseScopeChangeQueue;
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
	private ActivityReleaseScopeChangeQueue activityReleaseScopeChangeQueue;
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
	@Scheduled(fixedDelay = 10L)
	public void clearScopeCache() throws InterruptedException {
		log.info("处理活动发布范围改变任务 start");
		Integer activityId = activityReleaseScopeChangeQueue.pop();
		try {
			if (activityId == null) {
				return;
			}
			log.info("根据参数:{} 处理活动发布范围改变任务", activityId);
			List<String> externalIds = activityModuleService.listExternalIdsByActivityIdAndType(activityId, ModuleTypeEnum.WORK.getValue());
			if (CollectionUtils.isNotEmpty(externalIds)) {
				workApiService.clearActivityParticipateScopeCache(externalIds.stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList()));
			}
			log.info("处理活动发布范围改变任务 success");
		} catch (Exception e) {
			e.printStackTrace();
			log.error("根据参数:{} 处理活动发布范围改变任务 error:{}", activityId, e.getMessage());
			activityReleaseScopeChangeQueue.push(activityId);
		}

	}

}