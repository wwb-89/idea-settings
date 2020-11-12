package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.module.SignFormDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityModule;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**数据处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityHandleService
 * @description
 * @blame wwb
 * @date 2020-11-10 15:52:50
 */
@Slf4j
@Service
public class ActivityHandleService {

	@Resource
	private ActivityMapper activityMapper;

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityModuleService activityModuleService;

	@Resource
	private SignApiService signApiService;

	/**新增活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 15:54:16
	 * @param activity
	 * @param signForm
	 * @param activityModules
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(Activity activity, SignFormDTO signForm, List<ActivityModule> activityModules, LoginUserDTO loginUser) {
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		// 是否开启参与设置
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 添加报名签到
			Integer signId = handleSign(signForm);
			activity.setSignId(signId);
		}
		// 保存活动
		// 处理活动的状态, 新增的活动都是待发布的
		activity.setStatus(Activity.StatusEnum.WAIT_RELEASE.getValue());
		activity.setReleased(false);
		// 审核状态
		Boolean openAudit = activity.getOpenAudit();
		if (openAudit) {
			activity.setAuditStatus(Activity.AuditStatusEnum.WAIT_AUDIT.getValue());
		} else {
			activity.setAuditStatus(Activity.AuditStatusEnum.PASSED.getValue());
		}
		activity.setCreateUid(loginUser.getUid());
		activity.setCreateFid(loginUser.getFid());
		activityMapper.insert(activity);
		Integer activityId = activity.getId();
		// 处理模块
		handleActivityModules(activityId, activityModules);
	}

	private Integer handleSign(SignFormDTO signForm) {
		Integer signId = signForm.getId();
		if (signId == null) {
			// 新增报名签到
			signId = signApiService.create(signForm);
		} else {
			// 修改报名签到
			signApiService.update(signForm);
		}
		return signId;
	}

	/**修改活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 15:41:49
	 * @param activity
	 * @param signForm
	 * @param activityModules
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void edit(Activity activity, SignFormDTO signForm, List<ActivityModule> activityModules, LoginUserDTO loginUser) {
		activityValidationService.addInputValidate(activity);
		Integer activityId = activity.getId();
		Activity existActivity = activityValidationService.editAble(activityId, loginUser);
		// 更新报名签到
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 修改或更新
			Integer signId = activity.getSignId();
			signForm.setId(signId);
			handleSign(signForm);
			activity.setSignId(signId);
		}
		// 处理活动相关
		LocalDate startDate = activity.getStartDate();
		LocalDate endDate = activity.getEndDate();

		existActivity.setName(activity.getName());
		existActivity.setStartDate(startDate);
		existActivity.setEndDate(endDate);
		existActivity.setCoverCloudId(activity.getCoverCloudId());
		existActivity.setActivityForm(activity.getActivityForm());
		existActivity.setAddress(activity.getAddress());
		existActivity.setLongitude(activity.getLongitude());
		existActivity.setDimension(activity.getDimension());
		existActivity.setActivityClassifyId(activity.getActivityClassifyId());
		existActivity.setEnableSign(activity.getEnableSign());
		existActivity.setSignId(activity.getSignId());
		existActivity.setWebTemplateId(activity.getWebTemplateId());
		// 根据活动时间判断状态
		Integer status = calActivityStatus(startDate, endDate, existActivity.getStatus());
		existActivity.setStatus(status);
		activityMapper.update(existActivity, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activity)
		);
		// 处理模块
		handleActivityModules(activityId, activityModules);
	}

	private void handleActivityModules(Integer activityId, List<ActivityModule> activityModules) {
		if (CollectionUtils.isNotEmpty(activityModules)) {
			for (ActivityModule activityModule : activityModules) {
				Integer id = activityModule.getId();
				if (id == null) {
					activityModule.setActivityId(activityId);
				}
			}
			List<ActivityModule> adds = new ArrayList<>();
			List<ActivityModule> updates = new ArrayList<>();
			List<Integer> deletes = new ArrayList<>();
			// 查询活动已有的活动模块
			List<ActivityModule> existActivityModules = activityModuleService.listByActivityId(activityId);
			Map<Integer, ActivityModule> activityModuleIdValueMap = existActivityModules.stream().collect(Collectors.toMap(ActivityModule::getId, v -> v, (v1, v2) -> v2));
			if (CollectionUtils.isNotEmpty(existActivityModules)) {
				for (ActivityModule activityModule : activityModules) {
					Integer activityModuleId = activityModule.getId();
					if (activityModuleId == null) {
						// 新增
						adds.add(activityModule);
					} else {
						ActivityModule existActivityModule = activityModuleIdValueMap.get(activityModuleId);
						if (existActivityModule != null) {
							updates.add(activityModule);
						}
					}
				}
				List<Integer> activityModuleIds = activityModules.stream().map(ActivityModule::getId).collect(Collectors.toList());
				for (ActivityModule existActivityModule : existActivityModules) {
					Integer existActivityModuleId = existActivityModule.getId();
					if (!activityModuleIds.contains(existActivityModuleId)) {
						deletes.add(existActivityModuleId);
					}
				}
			} else {
				// 全部新增
				adds.addAll(existActivityModules);
			}
			if (CollectionUtils.isNotEmpty(adds)) {
				activityModuleService.batchAdd(activityModules);
			}
			if (CollectionUtils.isNotEmpty(updates)) {
				for (ActivityModule update : updates) {
					activityModuleService.update(update);
				}
			}
			if (CollectionUtils.isNotEmpty(deletes)) {
				activityModuleService.batchDelete(deletes);
			}
		} else {
			// 删除活动下的所有模块
			activityModuleService.deleteByActivityId(activityId);
		}
	}

	/**计算活动状态
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 16:57:27
	 * @param startDate
	 * @param endDate
	 * @param status
	 * @return java.lang.Integer
	*/
	private Integer calActivityStatus(LocalDate startDate, LocalDate endDate, Integer status) {
		LocalDate now = LocalDate.now();
		boolean guessEnded = now.isAfter(endDate);
		boolean guessOnGoing = now.isAfter(startDate) && now.isBefore(endDate);
		Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
		switch (statusEnum) {
			case RELEASED:
				// 已发布、进行中、已结束
			case ONGOING:
				// 已发布、进行中、已结束
			case ENDED:
				// 已发布、进行中、已结束
				if (guessEnded) {
					// 已结束
					return Activity.StatusEnum.ENDED.getValue();
				}
				if (guessOnGoing) {
					return Activity.StatusEnum.ONGOING.getValue();
				}
				return Activity.StatusEnum.RELEASED.getValue();
			default:
				return statusEnum.getValue();
		}
	}

}