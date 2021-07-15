package com.chaoxing.activity.service.activity.performance;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.PerformanceMapper;
import com.chaoxing.activity.model.Performance;
import com.chaoxing.activity.service.queue.user.UserActionQueueService;
import com.chaoxing.activity.util.enums.UserActionEnum;
import com.chaoxing.activity.util.enums.UserActionTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

/**活动表现
 * @author wwb
 * @version ver 1.0
 * @className ActivityPerformanceService
 * @description
 * @blame wwb
 * @date 2021-06-29 21:18:36
 */
@Slf4j
@Service
public class PerformanceService {

	@Resource
	private PerformanceMapper performanceMapper;

	@Resource
	private UserActionQueueService userActionQueueService;
	/***新增活动表现
	* @Description
	* @author huxiaolong
	* @Date 2021-06-30 10:04:39
	* @param
	* @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void addPerformance(Performance performance) {
		LocalDateTime now = LocalDateTime.now();
		performance.setCreateTime(now);
		performance.setUpdateTime(now);
		performanceMapper.insert(performance);
		userActionQueueService.push(
				new UserActionQueueService.QueueParamDTO(performance.getUid(),
						performance.getActivityId(),
						UserActionTypeEnum.PERFORMANCE,
						UserActionEnum.PERFORMANCE,
						String.valueOf(performance.getId()),
						performance.getCreateTime()));

	}

	/**根据activityId 和表现 ids 查询数据(含删除和未删除)
	* @Description
	* @author huxiaolong
	* @Date 2021-06-30 11:13:52
	* @param activityId
	* @param performanceIds
	* @return java.util.List<com.chaoxing.activity.model.Performance>
	*/
	public List<Performance> listAllPerformanceByIds(Integer activityId, List<Integer> performanceIds) {
		if (CollectionUtils.isEmpty(performanceIds)) {
			return Lists.newArrayList();
		}
		return performanceMapper.selectList(new QueryWrapper<Performance>()
				.lambda()
				.eq(Performance::getActivityId, activityId)
				.in(Performance::getId, performanceIds));
	}
}