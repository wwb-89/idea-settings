package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromPreachParamDTO;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.create.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.group.GroupCreateParamDTO;
import com.chaoxing.activity.dto.manager.group.GroupCreateResultDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.manager.wfwform.WfwFormCreateResultDTO;
import com.chaoxing.activity.dto.module.ClazzInteractionDTO;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.mapper.ActivityDetailMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.activity.engine.CustomAppConfigHandleService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.template.signup.SignUpWfwFormTemplateQueryService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.manager.ActivityPushReminderService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuHandleService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuQueryService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.activity.scope.ActivityClassService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.activity.stat.ActivityStatSummaryHandlerService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.event.ActivityDataChangeEventService;
import com.chaoxing.activity.service.event.ActivityStatusChangeEventService;
import com.chaoxing.activity.service.inspection.InspectionConfigHandleService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.GroupApiService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.ClazzInteractionApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.queue.event.activity.handler.CustomAppInterfaceCallQueueService;
import com.chaoxing.activity.service.tag.TagHandleService;
import com.chaoxing.activity.util.ApplicationContextHolder;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.ActivityModuleConstant;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.enums.*;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
	private ActivityDetailMapper activityDetailMapper;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityModuleService activityModuleService;
	@Resource
	private ActivityScopeService activityScopeService;
	@Resource
	private ActivityClassService activityClassService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private ActivityStatusService activityStatusService;
	@Resource
	private ActivityDataChangeEventService activityChangeEventService;
	@Resource
	private ActivityManagerService activityManagerService;
	@Resource
	private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;
	@Resource
	private InspectionConfigHandleService inspectionConfigHandleService;
	@Resource
	private ActivityComponentValueService activityComponentValueService;
	@Resource
	private ActivityMenuQueryService activityMenuQueryService;
	@Resource
	private ActivityMenuHandleService activityMenuHandleService;
	@Resource
	private ActivityMarketService activityMarketService;
	@Resource
	private SignUpConditionService signUpConditionService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private MhApiService mhApiService;
	@Resource
	private DistributedLock distributedLock;
	@Resource
	private WfwAreaApiService wfwAreaApiService;
	@Resource
	private MarketQueryService marketQueryService;
	@Resource
	private MarketHandleService marketHandleService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private TemplateComponentService templateComponentService;
	@Resource
	private ClassifyHandleService classifyHandleService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private GroupApiService groupApiService;
	@Resource
	private CloudApiService cloudApiService;
	@Resource
	private ActivityStatusChangeEventService activityStatusChangeEventService;
	@Resource
	private TagHandleService tagHandleService;
	@Resource
	private ClazzInteractionApiService clazzInteractionApiService;
	@Resource
	private ActivityPushReminderService activityPushReminderService;
	@Resource
	private CustomAppConfigHandleService customAppConfigHandleService;
	@Resource
	private CustomAppInterfaceCallQueueService customAppInterfaceCallQueueService;
	@Resource
	private WfwFormApiService wfwFormApiService;
	@Resource
	private SignUpWfwFormTemplateQueryService signUpWfwFormTemplateQueryService;

	/**新增活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-22 20:17:33
	 * @param activityCreateParamDto
	 * @param signCreateParamDto
	 * @param wfwRegionalArchitectureDtos
	 * @param loginUser
	 * @return java.lang.Integer
	*/
	@Transactional(rollbackFor = Exception.class)
	public Integer add(ActivityCreateParamDTO activityCreateParamDto, SignCreateParamDTO signCreateParamDto, List<WfwAreaDTO> wfwRegionalArchitectureDtos, LoginUserDTO loginUser) {
		return add(activityCreateParamDto, signCreateParamDto, wfwRegionalArchitectureDtos, null, loginUser, false);
	}

	/**新增活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 15:54:16
	 * @param activityCreateParamDto
	 * @param signCreateParamDto
	 * @param wfwRegionalArchitectureDtos
	 * @param loginUser
	 * @return java.lang.Integer 活动id
	 */
	@Transactional(rollbackFor = Exception.class)
	public Integer add(ActivityCreateParamDTO activityCreateParamDto, SignCreateParamDTO signCreateParamDto, List<WfwAreaDTO> wfwRegionalArchitectureDtos, List<Integer> releaseClassIds, LoginUserDTO loginUser, boolean isClone) {
		Activity activity = activityCreateParamDto.buildActivity();
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos) && CollectionUtils.isEmpty(releaseClassIds)) {
			throw new BusinessException(CollectionUtils.isEmpty(releaseClassIds) ? "请完善发布班级" : "请完善发布范围");
		}
		// 处理作品征集
		handleWork(activity, loginUser);
		// 添加小组
		handleGroup(activity, loginUser.getUid());
		// 添加报名签到
		handleSign(activity, signCreateParamDto, loginUser);
		// 处理活动的状态, 新增的活动都是待发布的
		activity.beforeCreate(loginUser.getUid(), loginUser.getRealName(), loginUser.getFid(), loginUser.getOrgName());
		activityMapper.insert(activity);
		Integer activityId = activity.getId();
		// 活动详情
		ActivityDetail activityDetail = activityCreateParamDto.buildActivityDetail(activityId);
		activityDetailMapper.insert(activityDetail);
		// 保存活动推送提醒
		ActivityPushReminder activityPushReminder = activityCreateParamDto.getActivityPushReminder();
		if (activity.getOpenPushReminder() && activityPushReminder != null) {
			activityPushReminder.setActivityId(activityId);
			activityPushReminderService.addOrUpdate(activityPushReminder);
		}
		// 保存活动报名的报名条件启用
		signUpConditionService.saveActivitySignUpConditionEnables(activityId, activityCreateParamDto.getSucTemplateComponentIds());
		signUpConditionService.saveActivitySignUpConditionsFromConditions(activityId, activityCreateParamDto.getSignUpConditions());
		// 保存自定义应用启用列表
		customAppConfigHandleService.saveActivityCustomAppEnables(activityId, activityCreateParamDto.getCustomAppEnableTplComponentIds());
		// 保存自定义组件值
		activityComponentValueService.saveActivityComponentValues(activityId, activityCreateParamDto.getActivityComponentValues());
		// 保存门户模板
		bindWebTemplate(activity, activityCreateParamDto.getWebTemplateId(), loginUser);
		// 考核配置
		boolean openInspectionConfig = Optional.ofNullable(activityCreateParamDto.getOpenInspectionConfig()).orElse(false);
		boolean existInspectionConfigCp = templateComponentService.existTemplateComponent(activity.getTemplateId(), "inspection_config");
		boolean disabledInspectionConfig = false;
		if (!openInspectionConfig || activityCreateParamDto.getInspectionConfigId() == null) {
			inspectionConfigHandleService.initInspectionConfig(activityId);
			if (existInspectionConfigCp && !openInspectionConfig) {
				// 模板存在考核管理但未开启考核配置，关闭默认考核管理菜单勾选
				disabledInspectionConfig = true;
			}
		}
		if (activityCreateParamDto.getInspectionConfigId() != null) {
			inspectionConfigHandleService.updateConfigActivityId(activityCreateParamDto.getInspectionConfigId(), activityId);
		}
		activityStatSummaryHandlerService.init(activityId);
		// 班级互动（一定要在报名签到处理完之后再进行）
		handleClazzInteraction(activity, signCreateParamDto, loginUser);
		// 活动菜单配置
		List<ActivityMenuConfig> activityMenus;
		if (isClone && activity.getOriginActivityId() != null) {
			activityMenus = activityMenuQueryService.listByActivityId(activity.getOriginActivityId());
		} else {
			activityMenus = activityMenuQueryService.listActivityAllDefaultMenus(activityId, activity.getTemplateId());
		}
		if (disabledInspectionConfig) {
			for (ActivityMenuConfig activityMenu : activityMenus) {
				if (Objects.equals(activityMenu.getMenu(), ActivityMenuEnum.BackendMenuEnum.RESULTS_MANAGE.getValue())) {
					activityMenu.setEnable(false);
					break;
				}
			}
		}
		Integer realActivityId = isClone && activity.getOriginActivityId() != null ? activity.getOriginActivityId() : activityId;
		activityMenuHandleService.configActivityDefaultMenu(realActivityId, activityMenus);
		// 若活动由市场所建，新增活动市场与活动关联
		activityMarketService.associate(activity);
		// 默认添加活动市场管理
		// 处理管理员
		handleManager(activity, activityCreateParamDto.getManagers(), loginUser);
		// 处理发布范围
		if (CollectionUtils.isNotEmpty(releaseClassIds)) {
			activityClassService.batchAddOrUpdate(activityId, releaseClassIds);
		} else {
			activityScopeService.batchAdd(activityId, wfwRegionalArchitectureDtos);
		}
		// 处理标签
		handleActivityTags(activity, activityCreateParamDto.getTagNames());
		// 活动改变
		activityChangeEventService.dataChange(activity, null, loginUser);
		// 接口调用
		customAppInterfaceCallQueueService.interfaceCall(activity, loginUser.getFid(), CustomAppInterfaceCall.CallTimingEnum.CREATE_CALL);
		return activityId;
	}

	private void handleManager(Activity activity, List<ActivityManager> activityManagers, LoginUserDTO loginUser) {
		ActivityManager activityManager = ActivityManager.buildCreator(activity);
		activityManagerService.initCreator(activityManager, loginUser);
		if (CollectionUtils.isEmpty(activityManagers)) {
			return;
		}
		Iterator<ActivityManager> iterator = activityManagers.iterator();
		while (iterator.hasNext()) {
			ActivityManager next = iterator.next();
			next.setActivityId(activity.getId());
			if (Objects.equals(next.getUid(), activity.getCreateUid())) {
				iterator.remove();
			}
		}
		if (CollectionUtils.isNotEmpty(activityManagers)) {
			activityManagerService.batchAdd(activityManagers, loginUser);
		}
	}

	/**新增并发布
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-25 17:18:51
	 * @param activityCreateParamDto
	 * @param signCreateParamDto
	 * @param wfwRegionalArchitectureDtos
	 * @param releaseClassIds
	 * @param loginUser
	 * @param isClone
	 * @return java.lang.Integer
	*/
	@Transactional(rollbackFor = Exception.class)
	public Integer addAndRelease(ActivityCreateParamDTO activityCreateParamDto, SignCreateParamDTO signCreateParamDto, List<WfwAreaDTO> wfwRegionalArchitectureDtos, List<Integer> releaseClassIds, LoginUserDTO loginUser, boolean isClone) {
		Integer activityId = add(activityCreateParamDto, signCreateParamDto, wfwRegionalArchitectureDtos, releaseClassIds, loginUser, isClone);
		release(activityId, loginUser.buildOperateUserDTO());
		return activityId;
	}

	/**处理作品征集
	 * @Description 如果开启了作品征集且没有创建则需要创建
	 * @author wwb
	 * @Date 2022-01-21 09:58:17
	 * @param activity
	 * @param loginUser
	 * @return void
	*/
	public void handleWork(Activity activity, LoginUserDTO loginUser) {
		Boolean openWork = Optional.ofNullable(activity.getOpenWork()).orElse(false);
		if (openWork && activity.getWorkId() == null) {
			WorkFormDTO workForm = WorkFormDTO.builder()
					.id(activity.getId())
					.name(activity.getName())
					.uid(loginUser.getUid())
					.wfwfid(loginUser.getFid())
					.startTime(DateUtils.date2Timestamp(activity.getStartTime()))
					.endTime(DateUtils.date2Timestamp(activity.getEndTime()))
					.build();
			Integer workId = workApiService.create(workForm);
			activity.setWorkId(workId);
		}

	}

	/**处理班级互动
	 * @Description 
	 * @author huxiaolong
	 * @Date 2021-12-02 18:09:03
	 * @param activity
	 * @param signCreateParamDto
	 * @param loginUser
	 * @return
	 */
	private void handleClazzInteraction(Activity activity, SignCreateParamDTO signCreateParamDto, LoginUserDTO loginUser) {
		boolean isTeachFlag = Activity.isTeachFlag(activity.getActivityFlag());
		if (!isTeachFlag) {
			return;
		}
		boolean needCreateClazzInteraction = Optional.ofNullable(activity.getOpenClazzInteraction()).orElse(false) && (activity.getClazzId() == null || activity.getCourseId() == null);
		if (needCreateClazzInteraction) {
			SignUpCreateParamDTO signUp = Optional.ofNullable(signCreateParamDto.getSignUps()).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
			Integer fillFormId = Optional.ofNullable(signUp).map(SignUpCreateParamDTO::getFillInfoFormId).orElse(null);
			String activityName = activity.getName();
			String coverUrl = activity.getCoverUrl();
			String flag = activity.getActivityFlag();
			Integer fid = activity.getCreateFid();
			Integer activityId = activity.getId();
			String startTime = Optional.ofNullable(activity.getStartTime()).map(v -> v.format(DateUtils.FULL_TIME_FORMATTER)).orElse(null);
			String endTime = Optional.ofNullable(activity.getEndTime()).map(v -> v.format(DateUtils.FULL_TIME_FORMATTER)).orElse(null);
			ClazzInteractionDTO clazzInteraction = clazzInteractionApiService.clazzCourseCreate(activityId, activityName, loginUser.getUid(), coverUrl, fillFormId, fid, flag, startTime, endTime);
			if (clazzInteraction == null) {
				return;
			}
			activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
					.eq(Activity::getId, activityId)
					.set(Activity::getClazzId, clazzInteraction.getClazzId())
					.set(Activity::getCourseId, clazzInteraction.getCourseId()));
		}
	}

	/**处理报名签到
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 15:46:49
	 * @param activity
	 * @param signCreateParam
	 * @param loginUser
	 * @return com.chaoxing.activity.dto.sign.create.SignCreateResultDTO
	 */
	private SignCreateResultDTO handleSign(Activity activity, SignCreateParamDTO signCreateParam, LoginUserDTO loginUser) {
		Integer fid = loginUser.getFid();
		Integer uid = loginUser.getUid();
		SignCreateResultDTO signCreateResultDto;
		signCreateParam.perfectName(activity.getName());
		signCreateParam.setMarketId(activity.getMarketId());
		// 活动创建页面时没有创建万能表单，在这里补充创建
		handleSignUps(fid, uid, signCreateParam);
		if (signCreateParam.getId() == null) {
			signCreateParam.perfectCreator(loginUser);
			signCreateResultDto = signApiService.create(signCreateParam);
		} else {
			signCreateParam.perfectCreator(loginUser);
			signCreateResultDto = signApiService.update(signCreateParam);
		}
		activity.setSignId(signCreateResultDto.getSignId());
		return signCreateResultDto;
	}

	private void handleSignUps(Integer fid, Integer uid, SignCreateParamDTO sign) {
		if (CollectionUtils.isEmpty(sign.getSignUps())) {
			return;
		}
		List<Integer> waitClearSignedUpIds = Lists.newArrayList();
		for (SignUpCreateParamDTO signUp : sign.getSignUps()) {
			if (signUp.getFillInfoFormId() == null) {
				// 根据模板类型和模板id获取模板
				SignUpFillInfoType.TypeEnum signUpFormTypeEnum = Objects.equals(SignUpFillInfoType.TypeEnum.APPROVAL.getValue(), signUp.getFormType()) ? SignUpFillInfoType.TypeEnum.APPROVAL : SignUpFillInfoType.TypeEnum.WFW_FORM;
				SignUpWfwFormTemplate.TypeEnum templateTypeEnum = Objects.equals(SignUpFillInfoType.TypeEnum.APPROVAL, signUpFormTypeEnum) ? SignUpWfwFormTemplate.TypeEnum.APPROVAL : SignUpWfwFormTemplate.TypeEnum.WFW_FORM;
				SignUpWfwFormTemplate template = signUpWfwFormTemplateQueryService.getByIdOrDefaultNormal(signUp.getWfwFormTemplateId(), templateTypeEnum);

				WfwFormCreateResultDTO wfwFormResult = wfwFormApiService.createWfwForm(fid, uid, template);
				signUp.setPcUrl(Optional.ofNullable(wfwFormResult).map(WfwFormCreateResultDTO::getPcUrl).orElse(null));
				signUp.setOpenAddr(Optional.ofNullable(wfwFormResult).map(WfwFormCreateResultDTO::getOpenAddr).orElse(null));
				signUp.setWechatUrl(Optional.ofNullable(wfwFormResult).map(WfwFormCreateResultDTO::getWechatUrl).orElse(null));
				signUp.setFillInfoFormId(Optional.ofNullable(wfwFormResult).map(WfwFormCreateResultDTO::getFormId).orElse(null));
				signUp.setWfwFormName(Optional.ofNullable(wfwFormResult).map(WfwFormCreateResultDTO::getName).orElse(null));
			}

			// 表单是否切换
			boolean fillFormSwitch = signUp.getId() != null && signUp.getOldFillInfoFormId() != null
					&& !Objects.equals(signUp.getFillInfoFormId(), signUp.getOldFillInfoFormId());
			if (fillFormSwitch){
				// 新旧填报表单不一致,	移除所有已报名数据
				waitClearSignedUpIds.add(signUp.getId());
			}
		}
		if (CollectionUtils.isNotEmpty(waitClearSignedUpIds)) {
			signApiService.clearSignedUpRecords(waitClearSignedUpIds);
		}
	}

	/**处理小组
	 * @Description 
	 * @author wwb
	 * @Date 2021-09-17 15:26:13
	 * @param activity
	 * @param createUid
	 * @return void
	*/
	private void handleGroup(Activity activity, Integer createUid) {
		Boolean openGroup = activity.getOpenGroup();
		String groupBbsid = activity.getGroupBbsid();
		if (StringUtils.isBlank(groupBbsid) && openGroup) {
			GroupCreateParamDTO groupCreateParamDto = new GroupCreateParamDTO(activity.getName(), createUid);
			GroupCreateResultDTO groupCreateResultDto = groupApiService.create(groupCreateParamDto);
			activity.setGroupBbsid(groupCreateResultDto.getBbsid());
		}
	}

	/**
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-03 18:24:46
	 * @param activityUpdateParamDto
	 * @param signCreateParam
	 * @param wfwRegionalArchitectureDtos
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public Activity edit(ActivityUpdateParamDTO activityUpdateParamDto, SignCreateParamDTO signCreateParam, final List<WfwAreaDTO> wfwRegionalArchitectureDtos, LoginUserDTO loginUser) {
		return edit(activityUpdateParamDto, signCreateParam, wfwRegionalArchitectureDtos, null, loginUser);
	}

	/**修改活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 15:41:49
	 * @param activityUpdateParamDto
	 * @param wfwRegionalArchitectureDtos
	 * @param signCreateParam
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public Activity edit(ActivityUpdateParamDTO activityUpdateParamDto, SignCreateParamDTO signCreateParam, final List<WfwAreaDTO> wfwRegionalArchitectureDtos, List<Integer> releaseClassIds, LoginUserDTO loginUser) {
		Activity activity = activityUpdateParamDto.buildActivity();
		activityValidationService.updateInputValidate(activity);
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos) && CollectionUtils.isEmpty(releaseClassIds)) {
			throw new BusinessException(CollectionUtils.isEmpty(releaseClassIds) ? "请完善发布班级" : "请完善发布范围");
		}
		Integer activityId = activity.getId();
		String activityEditLockKey = getActivityEditLockKey(activityId);
		return distributedLock.lock(activityEditLockKey, () -> {
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
			ActivityPushReminder activityPushReminder = activityUpdateParamDto.getActivityPushReminder();
			if (activity.getOpenPushReminder() && activityPushReminder != null) {
				activityPushReminder.setActivityId(activity.getId());
				activityPushReminderService.addOrUpdate(activityPushReminder);
			}
			activity.updatePerfectFromExistActivity(existActivity);
			// 添加小组
			handleGroup(activity, existActivity.getCreateUid());
			// 更新报名签到
			Integer signId = existActivity.getSignId();
			signCreateParam.setId(signId);
			handleSign(activity, signCreateParam, loginUser);
			// 处理活动封面
			activity.coverCloudIdChange(existActivity.getCoverCloudId());
			activityMapper.update(activity, new LambdaUpdateWrapper<Activity>()
					.eq(Activity::getId, activity.getId())
					// 一些可能为null的字段需要设置
					.set(Activity::getTimingReleaseTime, activity.getTimingReleaseTime())
					.set(Activity::getTimeLengthUpperLimit, activity.getTimeLengthUpperLimit())
					.set(Activity::getIntegral, activity.getIntegral())
					.set(Activity::getLongitude, activity.getLongitude())
					.set(Activity::getDimension, activity.getDimension())
					.set(Activity::getAddress, activity.getAddress())
			);
			// 考核配置
			boolean openInspectionConfig = Optional.ofNullable(activityUpdateParamDto.getOpenInspectionConfig()).orElse(false);
			activityMenuHandleService.updateActivityMenusByInspectionConfig(activityId, openInspectionConfig);
			// 班级互动
			ApplicationContextHolder.getBean(ActivityHandleService.class).handleClazzInteraction(activity, signCreateParam, loginUser);
			// 更新活动状态
			activityStatusService.statusUpdate(activityId);
			// 处理门户模版的绑定
			bindWebTemplate(existActivity, activity.getWebTemplateId(), loginUser);
			// 更新
			signUpConditionService.updateActivitySignUpEnables(activityId, activityUpdateParamDto.getSucTemplateComponentIds());
			signUpConditionService.updateActivitySignUpConditionsFromConditions(activityId, activityUpdateParamDto.getSignUpConditions());
			// 保存自定义应用启用列表
			customAppConfigHandleService.saveActivityCustomAppEnables(activityId, activityUpdateParamDto.getCustomAppEnableTplComponentIds());
			// 更新自定义组件的值
			activityComponentValueService.updateActivityComponentValues(activityId, activityUpdateParamDto.getActivityComponentValues());
			ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activityId);
			if (activityDetail == null) {
				activityDetail = activityUpdateParamDto.buildActivityDetail();
				activityDetailMapper.insert(activityDetail);
			}else {
				activityDetailMapper.update(null, new UpdateWrapper<ActivityDetail>()
						.lambda()
						.eq(ActivityDetail::getId, activityDetail.getId())
						.set(ActivityDetail::getIntroduction, activityUpdateParamDto.getIntroduction())
				);
			}
			// 处理发布范围
			if (CollectionUtils.isNotEmpty(releaseClassIds)) {
				activityClassService.batchAddOrUpdate(activityId, releaseClassIds);
			} else {
				activityScopeService.batchAdd(activityId, wfwRegionalArchitectureDtos);
			}
			// 处理标签
			handleActivityTags(existActivity, activityUpdateParamDto.getTagNames());
			// 活动改变
			activityChangeEventService.dataChange(activity, existActivity, loginUser);
			return activity;
		}, e -> {
			log.error("更新活动:{} error:{}", JSON.toJSONString(activity), e.getMessage());
			throw new BusinessException("更新活动失败");
		});
	}

	/**修改并发布
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-25 17:21:45
	 * @param activityUpdateParamDto
	 * @param signCreateParam
	 * @param wfwRegionalArchitectureDtos
	 * @param releaseClassIds
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	*/
	@Transactional(rollbackFor = Exception.class)
	public Activity editAndRelease(ActivityUpdateParamDTO activityUpdateParamDto, SignCreateParamDTO signCreateParam, final List<WfwAreaDTO> wfwRegionalArchitectureDtos, List<Integer> releaseClassIds, LoginUserDTO loginUser) {
		Activity activity = edit(activityUpdateParamDto, signCreateParam, wfwRegionalArchitectureDtos, releaseClassIds, loginUser);
		if (!activity.getReleased()) {
			release(activity.getId(), loginUser.buildOperateUserDTO());
			activity.setReleased(true);
		}
		return activity;
	}

	/**更新报名设置信息
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-04 17:15:42
	 * @param activityId
	 * @param sucTemplateComponentIds 开启报名条件templateComponentId
	 * @param signCreateParam
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateSignUp(Integer activityId, List<Integer> sucTemplateComponentIds, List<SignUpCondition> signUpConditions, SignCreateParamDTO signCreateParam, LoginUserDTO loginUser) {
		String activityEditLockKey = getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
			// 更新报名签到
			signCreateParam.setId(existActivity.getSignId());
			handleSign(existActivity, signCreateParam, loginUser);
			// 更新
			if (CollectionUtils.isNotEmpty(sucTemplateComponentIds)) {
				signUpConditionService.updateActivitySignUpEnables(activityId, sucTemplateComponentIds);
			}
			if (CollectionUtils.isNotEmpty(signUpConditions)) {
				signUpConditionService.updateActivitySignUpConditionsFromConditions(activityId, signUpConditions);
			}
			return null;
		}, e -> {
			log.error("更新报名:{} error:{}", JSON.toJSONString(signCreateParam.getSignUps()), e.getMessage());
			throw new BusinessException("更新活动失败");
		});
	}


	/**发布活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 15:41:48
	 * @param activityId
	 * @param operateUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void release(Integer activityId, OperateUserDTO operateUser) {
		Activity activity = activityValidationService.releaseAble(activityId, operateUser);
		activity.release(operateUser.getUid());
		activityStatusService.updateReleaseStatus(activity);
		// 发布自定义接口调用
		customAppInterfaceCallQueueService.interfaceCall(activity, operateUser.getFid(), CustomAppInterfaceCall.CallTimingEnum.RELEASE_CALL);
		Integer marketId = activity.getMarketId();
		if (marketId == null) {
			return;
		}
		activityPushReminderService.sendNotice(activity);
		// 更新活动市场下活动的状态
		activityMarketService.release(activity, marketId);
	}

	/**发布活动市场下的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 11:06:01
	 * @param activityId
	 * @param marketId
	 * @param operateUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void releaseMarketActivity(Integer activityId, Integer marketId, OperateUserDTO operateUser) {
		Activity activity = activityValidationService.activityExist(activityId);
		boolean sameMarketId = Objects.equals(activity.getMarketId(), marketId);
		if (sameMarketId) {
			release(activityId, operateUser);
		} else {
			activityMarketService.release(activity, marketId);
		}
	}

	/**取消发布（下架）
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 17:23:58
	 * @param activityId
	 * @param operateUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void cancelRelease(Integer activityId, OperateUserDTO operateUser) {
		Activity activity = activityValidationService.cancelReleaseAble(activityId, operateUser);
		activity.cancelRelease();
		activityStatusService.updateReleaseStatus(activity);
		// 下架自定义接口调用
		customAppInterfaceCallQueueService.interfaceCall(activity, operateUser.getFid(), CustomAppInterfaceCall.CallTimingEnum.CANCEL_RELEASE_CALL);
		Integer marketId = activity.getMarketId();
		if (marketId == null) {
			return;
		}
		// 更新活动市场下活动的状态
		activityMarketService.cancelRelease(activity, marketId);
	}

	/**取消发布活动市场下的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 11:18:04
	 * @param activityId
	 * @param marketId
	 * @param operateUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void cancelReleaseMarketActivity(Integer activityId, Integer marketId, OperateUserDTO operateUser) {
		Activity activity = activityValidationService.activityExist(activityId);
		boolean sameMarketId = Objects.equals(activity.getMarketId(), marketId);
		if (sameMarketId) {
			cancelRelease(activityId, operateUser);
		} else {
			activityMarketService.cancelRelease(activity, marketId);
		}
	}

	/**发布机构下的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 11:00:35
	 * @param activityId
	 * @param fid
	 * @param operateUser
	 * @return void
	*/
	public void releaseOrgActivity(Integer activityId, Integer fid, OperateUserDTO operateUser) {
		updateOrgActivityRelease(activityId, fid, true, operateUser);
	}
	
	/**下架机构下的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 11:01:22
	 * @param activityId
	 * @param fid
	 * @param operateUser
	 * @return void
	*/
	public void cancelReleaseOrgActivity(Integer activityId, Integer fid, OperateUserDTO operateUser) {
		updateOrgActivityRelease(activityId, fid, false, operateUser);
	}

	/**更新机构下关联了指定活动的市场活动的发布状态
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 11:31:27
	 * @param activityId
	 * @param fid
	 * @param release
	 * @param operateUser
	 * @return void
	*/
	private void updateOrgActivityRelease(Integer activityId, Integer fid, boolean release, OperateUserDTO operateUser) {
		List<Integer> marketIds = marketQueryService.listOrgAssociatedActivityMarketId(fid, activityId);
		if (CollectionUtils.isEmpty(marketIds)) {
			return;
		}
		for (Integer marketId : marketIds) {
			if (release) {
				releaseMarketActivity(activityId, marketId, operateUser);
			} else {
				cancelReleaseMarketActivity(activityId, marketId, operateUser);		
			}
		}
	}

	/**删除活动
	 * @Description
	 * 1、修改活动的状态为"已删除"
	 * 2、如果活动关联了活动市场，则修改活动市场关联表的状态为"已删除"
	 * @author wwb
	 * @Date 2021-11-16 10:04:34
	 * @param activityId
	 * @param operateUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void deleteActivity(Integer activityId, OperateUserDTO operateUser) {
		Activity activity =  activityValidationService.activityExist(activityId);
		deleteActivity(activity, operateUser);
	}

	/**删除活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 10:17:19
	 * @param activity
	 * @param operateUser
	 * @return void
	*/
	public void deleteActivity(Activity activity, OperateUserDTO operateUser) {
		Integer activityId = activity.getId();
		activityValidationService.deleteAble(activityId, operateUser);
		Integer oldStatus = activity.getStatus();
		if (activity.isDeleted()) {
			return;
		}
		activity.delete();
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, activity.getStatus())
		);
		activityMarketService.delete(activityId);
		// 活动状态改变
		activityStatusChangeEventService.statusChange(activity, oldStatus);
		// 删除活动自定义接口调用
		customAppInterfaceCallQueueService.interfaceCall(activity, operateUser.getFid(), CustomAppInterfaceCall.CallTimingEnum.DELETE_CALL);
	}

	/**删除活动市场下的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 10:05:11
	 * @param activityId
	 * @param marketId
	 * @param operateUser
	 * @return void
	*/
	public void deleteMarketActivity(Integer activityId, Integer marketId, OperateUserDTO operateUser) {
		Activity activity =  activityValidationService.activityExist(activityId);
		deleteMarketActivity(activity, marketId, operateUser);
	}

	/**删除活动市场下的活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-16 10:28:09
	 * @param activity
	 * @param marketId
	 * @param operateUser
	 * @return void
	*/
	public void deleteMarketActivity(Activity activity, Integer marketId, OperateUserDTO operateUser) {
		boolean sameMarketId = Objects.equals(activity.getMarketId(), marketId);
		if (sameMarketId) {
			deleteActivity(activity, operateUser);
		} else {
			// 只删除活动关联的市场状态为"已删除"
			activityMarketService.delete(activity.getId(), marketId);
		}
	}

	/**删除机构下关联了指定活动的市场关联的活动状态
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-12 17:40:07
	 * @param fid
	 * @param activityId
	 * @param uid
	 * @return com.chaoxing.activity.model.Activity
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteActivityUnderFid(Integer fid, Integer activityId, Integer uid) {
		LoginUserDTO loginUser = LoginUserDTO.buildDefault(uid, "", fid, "");
		OperateUserDTO operateUser = loginUser.buildOperateUserDTO();
		List<Integer> martketIds = marketQueryService.listOrgAssociatedActivityMarketId(fid, activityId);
		martketIds.forEach(marketId -> {
			deleteMarketActivity(activityId, marketId, operateUser);
		});
	}

	/**绑定模版
	 * @Description
	 * 1、根据模板信息创建相应的模块
	 * 2、传递模板id、wfwfid和活动id给门户克隆
	 * 3、门户克隆完成后调用活动引擎的接口来获取每个应用的数据来更新模板对应的应用的数据
	 * 4、完成后给活动引擎返回克隆后的应用的数据
	 * @author wwb
	 * @Date 2021-08-27 14:13:38
	 * @param activity
	 * @param newWebTemplateId
	 * @param loginUser
	 * @return void
	 */
	private void bindWebTemplate(Activity activity, Integer newWebTemplateId, LoginUserDTO loginUser) {
		// 如果门户模版id没有变化则忽略绑定模版的操作
		if (Objects.equals(activity.getWebTemplateId(), newWebTemplateId)) {
			return;
		}
		Integer activityId = activity.getId();
		// 创建模块
		createModuleByWebTemplateId(activity, newWebTemplateId);
		// 克隆
		MhCloneParamDTO mhCloneParam = packageMhCloneParam(activity, newWebTemplateId, loginUser);
		MhCloneResultDTO mhCloneResult = mhApiService.cloneTemplate(mhCloneParam);
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getWebTemplateId, newWebTemplateId)
				.set(Activity::getPageId, mhCloneResult.getPageId())
				.set(Activity::getPreviewUrl, mhCloneResult.getPreviewUrl())
				.set(Activity::getEditUrl, mhCloneResult.getEditUrl())
				.set(Activity::getWebsiteId, mhCloneResult.getWebsiteId())
		);
	}

	/**重新绑定模版
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-06 10:14:44
	 * @param activity
	 * @param webTemplateId
	 * @return void
	*/
	public void reBindWebTemplate(Activity activity, Integer webTemplateId) {
		LoginUserDTO loginUser = LoginUserDTO.buildDefault(activity.getCreateUid(), activity.getCreateUserName(), activity.getCreateFid(), activity.getCreateOrgName());
		// 创建模块
		createModuleByWebTemplateId(activity, webTemplateId);
		// 克隆
		MhCloneParamDTO mhCloneParam = packageMhCloneParam(activity, webTemplateId, loginUser);
		MhCloneResultDTO mhCloneResult = mhApiService.cloneTemplate(mhCloneParam);
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activity.getId())
				.set(Activity::getWebTemplateId, webTemplateId)
				.set(Activity::getPageId, mhCloneResult.getPageId())
				.set(Activity::getPreviewUrl, mhCloneResult.getPreviewUrl())
				.set(Activity::getEditUrl, mhCloneResult.getEditUrl())
				.set(Activity::getWebsiteId, mhCloneResult.getWebsiteId())
		);
	}

	private void handleActivityTags(Activity activity, List<String> tagNames) {
		Integer marketId = activity.getMarketId();
		if (marketId != null) {
			tagHandleService.marketAssociateTags(marketId, tagNames);
		} else {
			tagHandleService.orgAssociateTags(activity.getCreateFid(), tagNames);
		}
		tagHandleService.activityAssociateTags(activity.getId(), tagNames);
	}

	/**创建模块
	 * @Description
	 * 找到是本地数据源的图标应用
	 * @author wwb
	 * @Date 2020-11-23 20:36:17
	 * @param activity
	 * @param webTemplateId
	 * @return void
	 */
	private void createModuleByWebTemplateId(Activity activity, Integer webTemplateId) {
		Integer activityId = activity.getId();
		// 先删除活动的模块
		activityModuleService.deleteByActivityId(activityId);
		// 根据网页模板找到需要创建的模块id
		List<WebTemplateApp> webTemplateApps = webTemplateService.listLocalDataSourceAppByWebTemplateIdAppType(webTemplateId, MhAppTypeEnum.ICON);
		if (CollectionUtils.isEmpty(webTemplateApps)) {
			return;
		}
		List<ActivityModule> activityModules = Lists.newArrayList();
		for (WebTemplateApp webTemplateApp : webTemplateApps) {
			// 模板的应用id，克隆后会生成一个新的应用
			Integer appId = webTemplateApp.getAppId();
			List<WebTemplateAppData> webTemplateAppDataList = webTemplateApp.getWebTemplateAppDataList();
			if (CollectionUtils.isNotEmpty(webTemplateAppDataList)) {
				// 调用相应的接口创建模块，并将模块与活动关联保存起来
				Integer sequence = 1;
				for (WebTemplateAppData webTemplateAppData : webTemplateAppDataList) {
					ActivityModule activityModule = createModule(activity, appId, webTemplateAppData);
					activityModule.setSequence(sequence++);
					activityModules.add(activityModule);
				}
			}
		}
		if (CollectionUtils.isNotEmpty(activityModules)) {
			activityModuleService.batchAdd(activityModules);
		}
	}

	/**创建模块
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 10:33:47
	 * @param activity
	 * @param templateAppId
	 * @param webTemplateAppData
	 * @return com.chaoxing.activity.model.ActivityModule
	 */
	private ActivityModule createModule(Activity activity, Integer templateAppId, WebTemplateAppData webTemplateAppData) {
		ActivityModule activityModule = null;
		// 模块类型
		String type = webTemplateAppData.getType();
		ModuleTypeEnum moduleType = ModuleTypeEnum.fromValue(type);
		String appName = webTemplateAppData.getName();
		switch (moduleType) {
			case TPK:
				activityModule = activityModuleService.generateTpkModule(activity, templateAppId, appName);
				break;
			case STAR:
				activityModule = activityModuleService.generateStarModule(activity, templateAppId, appName);
				break;
			case WORK:
				activityModule = activityModuleService.generateWorkModule(activity, templateAppId, appName);
				break;
			case PUNCH:
				activityModule = activityModuleService.generatePunchModule(activity, templateAppId, appName);
				break;
			default:

		}
		return activityModule;
	}

	/**封装门户克隆模板使用的请求参数
	 * @Description
	 * @author wwb
	 * @Date 2020-12-21 16:12:23
	 * @param activity
	 * @param webTemplateId
	 * @param loginUser
	 * @return com.chaoxing.activity.dto.mh.MhCloneParamDTO
	 */
	public MhCloneParamDTO packageMhCloneParam(Activity activity, Integer webTemplateId, LoginUserDTO loginUser) {
		WebTemplate webTemplate = webTemplateService.webTemplateExist(webTemplateId);
		MhCloneParamDTO mhCloneParam = new MhCloneParamDTO();
		mhCloneParam.setTemplateId(webTemplate.getTemplateId());
		mhCloneParam.setWebsiteId(webTemplate.getWebsiteId());
		mhCloneParam.setWebsiteName(activity.getName());
		mhCloneParam.setOriginPageId(webTemplate.getOriginPageId());
		mhCloneParam.setUid(loginUser.getUid());
		mhCloneParam.setWfwfid(loginUser.getFid());
		mhCloneParam.setAppList(packageTemplateApps(activity, webTemplateId));
		return mhCloneParam;
	}

	/**封装应用模块列表
	 * @Description
	 * @author wwb
	 * @Date 2020-12-21 16:12:57
	 * @param activity
	 * @param webTemplateId
	 * @return java.util.List<com.chaoxing.activity.dto.mh.MhCloneParamDTO.MhAppDTO>
	 */
	private List<MhCloneParamDTO.MhAppDTO> packageTemplateApps(Activity activity, Integer webTemplateId) {
		List<WebTemplateApp> webTemplateApps = webTemplateService.listAppByWebTemplateId(webTemplateId);
		if (CollectionUtils.isEmpty(webTemplateApps)) {
			return Lists.newArrayList();
		}
		return webTemplateApps.stream().map(v -> packageMhAppDTO(activity, v)).collect(Collectors.toList());
	}

	/**封装应用模块
	 * @Description
	 * @author wwb
	 * @Date 2020-12-21 16:13:14
	 * @param activity
	 * @param webTemplateApp
	 * @return com.chaoxing.activity.dto.mh.MhCloneParamDTO.MhAppDTO
	 */
	private MhCloneParamDTO.MhAppDTO packageMhAppDTO(Activity activity, WebTemplateApp webTemplateApp) {
		MhCloneParamDTO.MhAppDTO mhApp = new MhCloneParamDTO.MhAppDTO();
		mhApp.setAppName(webTemplateApp.getAppName());
		Integer dataSourceType = webTemplateApp.getDataSourceType();
		MhAppDataSourceEnum mhAppDataSource = MhAppDataSourceEnum.fromValue(dataSourceType);
		mhApp.setDataType(mhAppDataSource.getValue());
		if (mhAppDataSource.equals(MhAppDataSourceEnum.LOCAL)) {
			// 本地数据源
			mhApp.setDataList(packageMhAppData(webTemplateApp, activity));
		} else {
			mhApp.setDataUrl(packageMhAppDataUrl(activity, webTemplateApp));
		}
		return mhApp;
	}

	/** 封装本地数据源数据
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 23:42:47
	 * @param webTemplateApp
	 * @param activity
	 * @return java.util.List<com.chaoxing.activity.dto.mh.MhCloneParamDTO.MhAppDataDTO>
	 */
	private List<MhCloneParamDTO.MhAppDataDTO> packageMhAppData(WebTemplateApp webTemplateApp, Activity activity) {
		Integer activityId = activity.getId();
		// 目前只处理图标
		Integer appId = webTemplateApp.getAppId();
		List<ActivityModule> activityModules = activityModuleService.listByActivityIdAndTemplateId(activityId, appId);
		if (CollectionUtils.isEmpty(activityModules)) {
			return Lists.newArrayList();
		}
		return activityModules.stream().map(activityModule -> MhCloneParamDTO.MhAppDataDTO.builder()
				.title(activityModule.getName())
				// 访问的url
				.url(String.format(ActivityModuleConstant.MODULE_ACCESS_URL, activityModule.getType(), activityModule.getExternalId()))
				.pageType(3)
				.coverUrl(cloudApiService.buildImageUrl(activityModule.getIconCloudId()))
				.build()).collect(Collectors.toList());
	}

	/**封装外部数据源数据
	 * @Description
	 * @author wwb
	 * @Date 2020-11-24 23:44:52
	 * @param activity
	 * @param webTemplateApp
	 * @return java.lang.String
	 */
	private String packageMhAppDataUrl(Activity activity, WebTemplateApp webTemplateApp) {
		Integer activityId = activity.getId();
		String dataType = webTemplateApp.getDataType();
		MhAppDataTypeEnum mhAppDataType = MhAppDataTypeEnum.fromValue(dataType);
		switch (mhAppDataType) {
			case ACTIVITY_COVER:
				return String.format(ActivityMhUrlConstant.ACTIVITY_COVER_URL, activityId);
			case ACTIVITY_INFO:
				return String.format(ActivityMhUrlConstant.ACTIVITY_INFO_URL, activityId);
			case ACTIVITY_SIGN_INFO:
				return String.format(ActivityMhUrlConstant.ACTIVITY_SIGN_INFO_URL, activityId);
			case SIGN_IN_UP:
				return String.format(ActivityMhUrlConstant.ACTIVITY_SIGN_URL, activityId);
			case ACTIVITY_LIST:
				return String.format(ActivityMhUrlConstant.ACTIVITY_RECOMMEND_URL, activityId);
			case DUAL_SELECT:
				return String.format(ActivityMhUrlConstant.DUAL_SELECT_URL, activityId);
			case activity_brief_info:
				return ActivityMhUrlConstant.ACTIVITY_BRIEF_INFO_URL;
			default:

		}
		return "";
	}

	/**更新活动的评价配置
	 * @Description
	 * @author wwb
	 * @Date 2021-03-08 16:22:35
	 * @param activityId
	 * @param openRating
	 * @param ratingNeedAudit
	 * @param loginUser
	 * @return void
	 */
	public void updateRatingConfig(Integer activityId, boolean openRating, boolean ratingNeedAudit, LoginUserDTO loginUser) {
		activityValidationService.manageAble(activityId, loginUser.getUid());
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getOpenRating, openRating)
				.set(Activity::getRatingNeedAudit, ratingNeedAudit)
		);
	}

	/**更新活动封面
	 * @Description
	 * @author wwb
	 * @Date 2021-03-26 16:25:00
	 * @param activityId
	 * @param coverUrl
	 * @return void
	 */
	public void updateActivityCoverUrl(Integer activityId, String coverUrl) {
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getCoverUrl, coverUrl)
		);
	}

	/**获取活动修改锁key
	 * @Description 修改活动的时候需要上锁
	 * @author wwb
	 * @Date 2021-03-26 20:27:43
	 * @param activityId
	 * @return java.lang.String
	 */
	public String getActivityEditLockKey(Integer activityId) {
		return CacheConstant.LOCK_CACHE_KEY_PREFIX + "activity" + CacheConstant.CACHE_KEY_SEPARATOR + activityId;
	}

	/**更新机构创建的活动（没有活动市场id）的分类id
	 * @Description
	 * @author wwb
	 * @Date 2021-07-19 17:32:11
	 * @param fid
	 * @param classifyId
	 * @param oldClassifyId
	 * @return void
	 */
	public void updateOrgActivityClassifyId(Integer fid, Integer classifyId, Integer oldClassifyId) {
		activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
				.eq(Activity::getCreateFid, fid)
				.eq(Activity::getMarketId, null)
				.eq(Activity::getActivityClassifyId, oldClassifyId)
				.set(Activity::getActivityClassifyId, classifyId)
		);
	}

	/**更新活动市场关联的活动的分类id
	 * @Description
	 * @author wwb
	 * @Date 2021-07-19 17:32:47
	 * @param marketId
	 * @param classifyId
	 * @param oldClassifyId
	 * @return void
	 */
	public void updateMarketActivityClassifyId(Integer marketId, Integer classifyId, Integer oldClassifyId) {
		activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
				.eq(Activity::getMarketId, marketId)
				.eq(Activity::getActivityClassifyId, oldClassifyId)
				.set(Activity::getActivityClassifyId, classifyId)
		);
	}

	/**置顶活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-10 17:44:18
	 * @param activityId
	 * @param marketId
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void setActivityTop(Integer activityId, Integer marketId) {
		if (activityId == null || marketId == null) {
			return;
		}
		activityMarketService.updateActivityTop(activityId, marketId, Boolean.TRUE);
	}

	/**取消活动置顶
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-10 17:44:06
	 * @param activityId
	 * @param marketId
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void cancelActivityTop(Integer activityId, Integer marketId) {
		if (activityId == null || marketId == null) {
			return;
		}
		activityMarketService.updateActivityTop(activityId, marketId, Boolean.FALSE);
	}

	/**宣讲会创建活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-20 19:53:36
	 * @param activityCreateDto
	 * @param loginUser
	 * @return com.chaoxing.activity.model.Activity
	 */
	@Transactional(rollbackFor = Exception.class)
	public Activity newSharedActivity(ActivityCreateFromPreachParamDTO activityCreateDto, LoginUserDTO loginUser) {
		Integer createFid = activityCreateDto.getFid();
		String flag = activityCreateDto.getFlag();
		Integer marketId = marketHandleService.getOrCreateMarket(createFid, Activity.ActivityFlagEnum.fromValue(flag), loginUser);
		Template template = templateQueryService.getMarketFirstTemplate(marketId);
		ActivityCreateParamDTO activityCreateParam = activityCreateDto.getActivityInfo();
		activityCreateParam.setMarketId(marketId);
		activityCreateParam.setTemplateId(template.getId());
		activityCreateParam.setActivityFlag(flag);
		String activityName = activityCreateParam.getName();
		// 判断是否开启报名、报名填报信息
		SignCreateParamDTO signCreateParam = SignCreateParamDTO.builder().name(activityName).build();
		if (activityCreateDto.getOpenSignUp()) {
			SignUpCreateParamDTO signUpCreateParam = SignUpCreateParamDTO.buildDefault();
			signUpCreateParam.setFillInfo(activityCreateDto.getOpenFillFormInfo());
			signCreateParam.setSignUps(Lists.newArrayList(signUpCreateParam));
		}
		Integer activityId = add(activityCreateParam, signCreateParam, wfwAreaApiService.listByFid(createFid), loginUser);
		// 若活动由市场所建，新增活动市场与活动关联，共享活动到其他机构
		List<Integer> shareFids = Optional.ofNullable(activityCreateDto.getSharedFids()).filter(StringUtils::isNotBlank)
				.map(v -> Arrays.stream(v.split(",")).map(Integer::valueOf).collect(Collectors.toList())).orElse(Lists.newArrayList());
		Activity activity = activityQueryService.getById(activityId);
		activityMarketService.shareActivityToFids(activity, shareFids, loginUser.buildOperateUserDTO());
		return activity;
	}

	/**todo 待调整
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-20 18:40:04
	 * @param activityCreateDTO
	 * @return void
	 */
	public void updatePartialActivityInfo(ActivityCreateFromPreachParamDTO activityCreateDTO, LoginUserDTO loginUser) {
		ActivityCreateParamDTO activityParam = activityCreateDTO.getActivityInfo();
		Activity activity = activityQueryService.getById(activityParam.getId());
		// 报名签到
		Integer signId = activity.getSignId();
		SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getCreateById(signId);
		}
		ActivityUpdateParamDTO activityUpdateParam = ActivityUpdateParamDTO.buildActivityUpdateParam(activity, activityParam);
		edit(activityUpdateParam, sign, wfwAreaApiService.listByFid(activityCreateDTO.getFid()), loginUser);
	}

	/**删除万能表单关联的活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-27 17:56:10
	 * @param formId
	 * @param formUserId
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteWfwFormActivity(Integer formId, Integer formUserId) {
		if (formId == null || formUserId == null) {
			return;
		}
		Activity activity = activityQueryService.getByFormUserId(formId, formUserId);
		if (activity != null) {
			LoginUserDTO loginUser = LoginUserDTO.buildDefault(activity.getCreateUid(), "", activity.getCreateFid(), "");
			deleteActivity(activity, loginUser.buildOperateUserDTO());
		}
	}

	/**活动克隆
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-06 17:21:35
	 * @param activityId
	 * @param fid
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void cloneActivityToOrg(Integer activityId, Integer fid, List<WfwAreaDTO> releaseScopes, LoginUserDTO loginUser) {
		Integer uid = loginUser.getUid();
		Activity originActivity;
		if (activityId == null || fid == null ||  (originActivity = activityQueryService.getById(activityId)) == null) {
			return;
		}
		// 判断活动市场
		Template targetTemplate = templateQueryService.getOrgTemplateByActivityFlag(fid, Activity.ActivityFlagEnum.fromValue(originActivity.getActivityFlag()));
		// 有模板肯定有市场，故只需要处理模板不存在的情况
		if (targetTemplate == null) {
			// 克隆市场，且克隆模板
			Market newMarket = marketHandleService.cloneMarketAndTemplate(originActivity.getMarketId(), originActivity.getTemplateId(), fid, loginUser);
			// 查询模板信息
			targetTemplate = templateQueryService.getMarketFirstTemplate(newMarket.getId());
		}
		Integer marketId = targetTemplate.getMarketId();
		ActivityCreateParamDTO targetActivity = ActivityCreateParamDTO.cloneFromActivity(originActivity);

		// 重新设定活动的活动市场和模板id
		targetActivity.setOriginActivityId(originActivity.getId());
		targetActivity.setTemplateId(targetTemplate.getId());
		targetActivity.setMarketId(marketId);
		// 处理克隆分类
		if (StringUtils.isNotBlank(originActivity.getActivityClassifyName())) {
			Classify classify = classifyHandleService.getOrAddMarketClassify(marketId, originActivity.getActivityClassifyName());
			targetActivity.setActivityClassifyId(classify.getId());
		}
		// 判断是否开启作品征集
		if (targetActivity.getOpenWork()) {
			targetActivity.setWorkId(workApiService.createDefault(uid, fid));
		}
		// 阅读默认关闭
		targetActivity.setOpenReading(Boolean.FALSE);
		targetActivity.setReadingId(null);
		targetActivity.setReadingModuleId(null);
		// 主办方
		targetActivity.setOrganisers(loginUser.getOrgName());
		activityDetailMapper.selectList(new LambdaQueryWrapper<ActivityDetail>()
				.eq(ActivityDetail::getActivityId, targetActivity.getOriginActivityId()))
				.stream().findFirst()
				.ifPresent(originDetail -> targetActivity.setIntroduction(originDetail.getIntroduction()));
		// 报名签到
		SignCreateParamDTO signCreateParam = SignCreateParamDTO.builder().name(targetActivity.getName()).build();
		// 将克隆的活动保存
		ApplicationContextHolder.getBean(ActivityHandleService.class).add(targetActivity, signCreateParam, releaseScopes, loginUser);
	}

	/**删除活动市场下的所有活动
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-01 16:53:26
	 * @param marketId
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void deleteByMarketId(Integer marketId) {
		List<Activity> activities = activityQueryService.listByMarketId(marketId);
		if (CollectionUtils.isEmpty(activities)) {
			return;
		}
		activities.forEach(v -> {
			LoginUserDTO loginUser = LoginUserDTO.buildDefault(v.getCreateUid(), v.getCreateFid());
			deleteMarketActivity(v, marketId, loginUser.buildOperateUserDTO());
		});
	}

	/**
	 * 更新活动归档状态
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-12-01 12:05:17
	 * @param activityId
	 * @param archive
	 * @return
	 */
    public void updateActivityArchive(Integer activityId, boolean archive) {
		activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
				.eq(Activity::getId, activityId)
				.set(Activity::getArchived, archive));
    }
}