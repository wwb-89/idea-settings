package com.chaoxing.activity.service.inspection;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.InspectionConfigDetailMapper;
import com.chaoxing.activity.mapper.InspectionConfigMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.InspectionConfig;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.chaoxing.activity.model.UserResult;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueue;
import com.chaoxing.activity.service.queue.user.UserResultQueue;
import com.chaoxing.activity.service.user.result.UserResultQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**考核配置处理服务
 * @author wwb
 * @version ver 1.0
 * @className InspectionConfigHandleService
 * @description
 * @blame wwb
 * @date 2021-06-16 15:26:50
 */
@Slf4j
@Service
public class InspectionConfigHandleService {

	@Resource
	private InspectionConfigMapper inspectionConfigMapper;
	@Resource
	private InspectionConfigDetailMapper inspectionConfigDetailMapper;

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;
	@Resource
	private ActivityInspectionResultDecideQueue activityInspectionResultDecideQueueService;
	@Resource
	private UserResultQueue userResultQueueService;
	@Resource
	private UserResultQueryService userResultQueryService;

	/**初始化考核配置
	 * @Description 创建活动的时候初始化一个考核配置
	 * @author wwb
	 * @Date 2021-07-01 11:19:03
	 * @param activityId
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void initInspectionConfig(Integer activityId) {
		InspectionConfig existInspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		if (existInspectionConfig != null) {
			// 已经存在就不初始化新的了
			return;
		}
		// 构建默认的考核计划
		InspectionConfig inspectionConfig = InspectionConfig.buildDefault(activityId);
		inspectionConfigMapper.insert(inspectionConfig);
		Integer configId = inspectionConfig.getId();
		// 构建默认的积分规则
		InspectionConfigDetail inspectionConfigDetail = InspectionConfigDetail.buildDefault(configId);
		inspectionConfigDetailMapper.insert(inspectionConfigDetail);
	}

	/**配置
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-16 17:50:12
	 * @param inspectionConfig
	 * @param inspectionConfigDetails
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public Integer config(InspectionConfig inspectionConfig, List<InspectionConfigDetail> inspectionConfigDetails, LoginUserDTO loginUser) {
		if (inspectionConfig.getId() == null) {
			return add(inspectionConfig, inspectionConfigDetails);
		} else {
			return edit(inspectionConfig, inspectionConfigDetails, loginUser);
		}
	}

	public void reCalculateScore(Integer activityId) {
		// 重新计算得分
		List<UserResult> userResults = userResultQueryService.listByActivityId(activityId);
		if (CollectionUtils.isNotEmpty(userResults)) {
			for (UserResult userResult : userResults) {
				userResultQueueService.push(new UserResultQueue.QueueParamDTO(userResult.getUid(), userResult.getActivityId()));
			}
		}
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-19 11:33:58
	 * @param inspectionConfig
	 * @param inspectionConfigDetails
	 * @return java.lang.Integer
	 */
	public Integer add(InspectionConfig inspectionConfig, List<InspectionConfigDetail> inspectionConfigDetails) {
		inspectionConfigMapper.insert(inspectionConfig);
		Integer configId = inspectionConfig.getId();
		for (InspectionConfigDetail inspectionConfigDetail : inspectionConfigDetails) {
			inspectionConfigDetail.setConfigId(configId);
			inspectionConfigDetailMapper.insert(inspectionConfigDetail);
		}
		return configId;
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-19 11:34:05
	 * @param inspectionConfig
	 * @param inspectionConfigDetails
	 * @param loginUser
	 * @return java.lang.Integer
	 */
	public Integer edit(InspectionConfig inspectionConfig, List<InspectionConfigDetail> inspectionConfigDetails, LoginUserDTO loginUser) {
		Integer activityId = inspectionConfig.getActivityId();
		InspectionConfig existInspectionConfig;
		Activity activity = null;
		if (activityId == null) {
			existInspectionConfig = inspectionConfigQueryService.getByConfigId(inspectionConfig.getId());
		} else {
			activity = activityValidationService.manageAble(activityId, loginUser.getUid());
			existInspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		}

		boolean standardChanged = isStandardChanged(inspectionConfig, existInspectionConfig);
		// 更新
		inspectionConfigMapper.update(null, new UpdateWrapper<InspectionConfig>()
				.lambda()
				.eq(InspectionConfig::getId, existInspectionConfig.getId())
				.set(InspectionConfig::getPassDecideWay, inspectionConfig.getPassDecideWay())
				.set(InspectionConfig::getDecideValue, inspectionConfig.getDecideValue())
		);

		Integer configId = inspectionConfig.getId();
		List<InspectionConfigDetail> oldInspectionConfigDetails = inspectionConfigQueryService.listDetailByActivityId(activityId);
		boolean ruleChanged = isRuleChanged(inspectionConfigDetails, oldInspectionConfigDetails);
		for (InspectionConfigDetail inspectionConfigDetail : inspectionConfigDetails) {
			inspectionConfigDetail.setConfigId(configId);
			if (inspectionConfigDetail.getId() == null) {
				inspectionConfigDetailMapper.insert(inspectionConfigDetail);
			} else {
				inspectionConfigDetailMapper.update(null, new UpdateWrapper<InspectionConfigDetail>()
						.lambda()
						.eq(InspectionConfigDetail::getId, inspectionConfigDetail.getId())
						.set(InspectionConfigDetail::getScore, inspectionConfigDetail.getScore())
						.set(InspectionConfigDetail::getUpperLimit, inspectionConfigDetail.getUpperLimit())
						.set(InspectionConfigDetail::getDeleted, inspectionConfigDetail.getDeleted())
				);
			}
		}
		if (ruleChanged) {
			reCalculateScore(activityId);
		}
		// 如果活动已经结束需要重新判定成绩
		if (activity != null && activity.isEnded() && standardChanged) {
			activityInspectionResultDecideQueueService.push(activityId);
		}
		return configId;
	}

	/**更新考核配置活动id
	* @Description
	* @author huxiaolong
	* @Date 2021-10-19 11:35:22
	* @param configId
	* @param activityId
	* @return void
	*/
	public void updateConfigActivityId(Integer configId, Integer activityId) {
		inspectionConfigMapper.update(null, new LambdaUpdateWrapper<InspectionConfig>()
				.eq(InspectionConfig::getId, configId).set(InspectionConfig::getActivityId, activityId));
	}

	/**标准是否改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-25 17:59:48
	 * @param inspectionConfig
	 * @param oldInspectionConfig
	 * @return boolean
	*/
	private boolean isStandardChanged(InspectionConfig inspectionConfig, InspectionConfig oldInspectionConfig) {
		if (oldInspectionConfig == null) {
			return true;
		}
		if (!Objects.equals(inspectionConfig.getPassDecideWay(), oldInspectionConfig.getPassDecideWay())) {
			return true;
		}
		BigDecimal decideValue = inspectionConfig.getDecideValue();
		decideValue = Optional.ofNullable(decideValue).orElse(BigDecimal.ZERO);
		BigDecimal oldDecideValue = oldInspectionConfig.getDecideValue();
		oldDecideValue = Optional.ofNullable(oldDecideValue).orElse(BigDecimal.ZERO);
		return decideValue.compareTo(oldDecideValue) != 0;
	}

	/**规则是否改变
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-25 18:00:55
	 * @param inspectionConfigDetails
	 * @param oldInspectionConfigDetails
	 * @return boolean
	*/
	private boolean isRuleChanged(List<InspectionConfigDetail> inspectionConfigDetails, List<InspectionConfigDetail> oldInspectionConfigDetails) {
		if (CollectionUtils.isEmpty(oldInspectionConfigDetails)) {
			return true;
		}
		if (inspectionConfigDetails.size() != oldInspectionConfigDetails.size()) {
			return true;
		}
		Map<String, InspectionConfigDetail> keyObjectMap = inspectionConfigDetails.stream().collect(Collectors.toMap(v -> v.getActionType() + v.getAction(), v -> v, (v1, v2) -> v2));
		Map<String, InspectionConfigDetail> oldKeyObjectMap = oldInspectionConfigDetails.stream().collect(Collectors.toMap(v -> v.getActionType() + v.getAction(), v -> v, (v1, v2) -> v2));
		for (Map.Entry<String, InspectionConfigDetail> entry : keyObjectMap.entrySet()) {
			String key = entry.getKey();
			InspectionConfigDetail value = entry.getValue();
			InspectionConfigDetail oldValue = oldKeyObjectMap.get(key);
			if (oldValue == null) {
				return true;
			}
			BigDecimal score = Optional.ofNullable(value.getScore()).orElse(BigDecimal.ZERO);
			BigDecimal upperLimit = Optional.ofNullable(value.getUpperLimit()).orElse(BigDecimal.ZERO);
			Boolean deleted = Optional.ofNullable(value.getDeleted()).orElse(false);

			BigDecimal oldScore = Optional.ofNullable(oldValue.getScore()).orElse(BigDecimal.ZERO);
			BigDecimal oldUpperLimit = Optional.ofNullable(oldValue.getUpperLimit()).orElse(BigDecimal.ZERO);
			Boolean oldDeleted = Optional.ofNullable(oldValue.getDeleted()).orElse(false);
			if (score.compareTo(oldScore) != 0) {
				return true;
			}
			if (upperLimit.compareTo(oldUpperLimit) != 0) {
				return true;
			}
			if (!Objects.equals(deleted, oldDeleted)) {
				return true;
			}
		}
		return false;
	}

}
