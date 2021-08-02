package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateResultDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.mapper.ActivityDetailMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.event.ActivityChangeEventService;
import com.chaoxing.activity.service.inspection.InspectionConfigHandleService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueueService;
import com.chaoxing.activity.service.queue.activity.ActivityWebsiteIdSyncQueueService;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueueService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.ActivityModuleConstant;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.enums.MhAppDataSourceEnum;
import com.chaoxing.activity.util.enums.MhAppDataTypeEnum;
import com.chaoxing.activity.util.enums.MhAppTypeEnum;
import com.chaoxing.activity.util.enums.ModuleTypeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
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
	private WebTemplateService webTemplateService;
	@Resource
	private ActivityStatusService activityStatusService;
	@Resource
	private ActivityChangeEventService activityChangeEventService;
	@Resource
	private ActivityManagerService activityManagerService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private ActivityWebsiteIdSyncQueueService activityWebsiteIdSyncQueueService;
	@Resource
	private ActivityStatSummaryHandlerService activityStatSummaryHandlerService;
	@Resource
	private ActivityInspectionResultDecideQueueService activityInspectionResultDecideQueueService;
	@Resource
	private InspectionConfigHandleService inspectionConfigHandleService;
	@Resource
	private ActivityComponentValueService activityComponentValueService;
	@Resource
	private BlacklistAutoAddQueueService blacklistAutoAddQueueService;

	@Resource
	private SignUpConditionService signUpConditionService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private MhApiService mhApiService;
	@Resource
	private DistributedLock distributedLock;

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
	public Integer add(ActivityCreateParamDTO activityCreateParamDto, SignCreateParamDTO signCreateParamDto, List<WfwAreaDTO> wfwRegionalArchitectureDtos, LoginUserDTO loginUser) {
		Activity activity = activityCreateParamDto.buildActivity();
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos)) {
			throw new BusinessException("请选择发布范围");
		}
		// 添加报名签到
		SignCreateResultDTO signCreateResult = handleSign(activity, signCreateParamDto, loginUser);
		activity.setSignId(signCreateResult.getSignId());
		// 添加作品征集
		handleWork(activity, loginUser);
		// 处理活动的状态, 新增的活动都是待发布的
		activity.beforeCreate(loginUser.getUid(), loginUser.getRealName(), loginUser.getFid(), loginUser.getOrgName());
		activityMapper.insert(activity);
		Integer activityId = activity.getId();
		// 保存活动报名的报名条件启用
		signUpConditionService.saveActivitySignUpEnables(activityId, activityCreateParamDto.getSucTemplateComponentIds());
		// 保存自定义组件值
		activityComponentValueService.saveActivityComponentValues(activityId, activityCreateParamDto.getActivityComponentValues());
		// 保存门户模板
		MhCloneResultDTO mhCloneResult = bindWebTemplate(activityId, activityCreateParamDto.getWebTemplateId(), loginUser);

		inspectionConfigHandleService.initInspectionConfig(activityId);
		activityStatSummaryHandlerService.init(activityId);
		// 活动详情
		ActivityDetail activityDetail = activityCreateParamDto.buildActivityDetail(activityId);
		activityDetailMapper.insert(activityDetail);
		// 添加管理员
		ActivityManager activityManager = ActivityManager.buildCreator(activity);
		activityManagerService.add(activityManager, loginUser);
		// 处理发布范围
		activityScopeService.batchAdd(activityId, wfwRegionalArchitectureDtos);
		// 活动改变
		activityChangeEventService.dataChange(activity, null, loginUser);
		return activityId;
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
		SignCreateResultDTO signCreateResultDto;
		signCreateParam.perfectName(activity.getName());
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

	/**处理作品征集
	 * @Description
	 * @author wwb
	 * @Date 2021-04-09 15:04:39
	 * @param activity
	 * @param loginUser
	 * @return void
	 */
	private void handleWork(Activity activity, LoginUserDTO loginUser) {
		Boolean openWork = Optional.ofNullable(activity.getOpenWork()).orElse(Boolean.FALSE);
		if (openWork) {
			Integer workId = activity.getWorkId();
			if (workId == null) {
				// 创建作品征集
				WorkFormDTO workForm = WorkFormDTO.builder()
						.name(activity.getName())
						.wfwfid(loginUser.getFid())
						.uid(loginUser.getUid())
						.startTime(DateUtils.date2Timestamp(activity.getStartTime()))
						.endTime(DateUtils.date2Timestamp(activity.getEndTime()))
						.build();
				workId = workApiService.create(workForm);
				activity.setWorkId(workId);
			}
		}
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
	public void edit(ActivityUpdateParamDTO activityUpdateParamDto, SignCreateParamDTO signCreateParam, final List<WfwAreaDTO> wfwRegionalArchitectureDtos, LoginUserDTO loginUser) {
		Activity activity = activityUpdateParamDto.buildActivity();
		activityValidationService.updateInputValidate(activity);
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos)) {
			throw new BusinessException("请选择发布范围");
		}
		Integer activityId = activity.getId();
		String activityEditLockKey = getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
			// 更新报名签到
			Integer signId = existActivity.getSignId();
			signCreateParam.setId(signId);
			handleSign(activity, signCreateParam, loginUser);
			// 征集相关
			handleWork(activity, loginUser);
			// 处理活动相关
			if (!Objects.equals(existActivity.getCoverCloudId(), activity.getCoverCloudId())) {
				activity.coverCloudIdChange();
			}
			activityMapper.update(activity, new LambdaUpdateWrapper<Activity>()
					.eq(Activity::getId, activity.getId())
					// 一些可能为null的字段需要设置
					.set(Activity::getTimingReleaseTime, activity.getTimingReleaseTime())
					.set(Activity::getTimeLengthUpperLimit, activity.getTimeLengthUpperLimit())
					.set(Activity::getIntegral, activity.getIntegral())
			);
			// 更新
			signUpConditionService.updateActivitySignUpEnables(activityId, activityUpdateParamDto.getSucTemplateComponentIds());
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
			activityScopeService.batchAdd(activityId, wfwRegionalArchitectureDtos);
			// 活动改变
			activityChangeEventService.dataChange(activity, existActivity, loginUser);
			return null;
		}, e -> {
			log.error("更新活动:{} error:{}", JSON.toJSONString(activity), e.getMessage());
			throw new BusinessException("更新活动失败");
		});
	}

	/**发布活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 15:41:48
	 * @param activityId
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void release(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.releaseAble(activityId, loginUser);
		activity.release(loginUser.getUid());
		activityStatusService.updateReleaseStatus(activity);
	}

	/**取消发布（下架）
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 17:23:58
	 * @param activityId
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void cancelRelease(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.cancelReleaseAble(activityId, loginUser);
		activity.cancelRelease();
		activityStatusService.updateReleaseStatus(activity);
	}

	/**删除活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-19 12:27:35
	 * @param activityId
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer activityId, LoginUserDTO loginUser) {
		// 验证是否能删除
		Activity activity = activityValidationService.deleteAble(activityId, loginUser);
		activity.delete();
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, activity.getStatus())
		);
		// 活动状态改变
		activityChangeEventService.statusChange(activity);
	}

	/**绑定模板
	 * @Description
	 * 1、根据模板信息创建相应的模块
	 * 2、传递模板id、wfwfid和活动id给门户克隆
	 * 3、门户克隆完成后调用活动引擎的接口来获取每个应用的数据来更新模板对应的应用的数据
	 * 4、完成后给活动引擎返回克隆后的应用的数据
	 * @author wwb
	 * @Date 2020-11-13 15:36:28
	 * @param activityId
	 * @param webTemplateId
	 * @param loginUser
	 * @return com.chaoxing.activity.dto.mh.MhCloneResultDTO
	 */
	@Transactional(rollbackFor = Exception.class, propagation = Propagation.REQUIRED)
	public MhCloneResultDTO bindWebTemplate(Integer activityId, Integer webTemplateId, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.activityExist(activityId);
		// 如果已经选择了模板就不能再选择
		Integer webTemplateId1 = activity.getWebTemplateId();
		if (webTemplateId1 != null) {
			throw new BusinessException("活动已经选择了模板");
		}
		// 创建模块
		createModuleByWebTemplateId(activityId, webTemplateId, loginUser);
		// 克隆
		MhCloneParamDTO mhCloneParam = packageMhCloneParam(activity, webTemplateId, loginUser);
		MhCloneResultDTO mhCloneResult = mhApiService.cloneTemplate(mhCloneParam);
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getWebTemplateId, webTemplateId)
				.set(Activity::getPageId, mhCloneResult.getPageId())
				.set(Activity::getPreviewUrl, mhCloneResult.getPreviewUrl())
				.set(Activity::getEditUrl, mhCloneResult.getEditUrl())
		);
		activityWebsiteIdSyncQueueService.add(activityId);
		return mhCloneResult;
	}

	/**创建模块
	 * @Description
	 * 找到是本地数据源的图标应用
	 * @author wwb
	 * @Date 2020-11-23 20:36:17
	 * @param activityId
	 * @param webTemplateId
	 * @param loginUser
	 * @return void
	 */
	private void createModuleByWebTemplateId(Integer activityId, Integer webTemplateId, LoginUserDTO loginUser) {
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
					ActivityModule activityModule = createModule(activityId, appId, webTemplateAppData, loginUser);
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
	 * @param activityId
	 * @param templateAppId
	 * @param webTemplateAppData
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityModule
	 */
	private ActivityModule createModule(Integer activityId, Integer templateAppId, WebTemplateAppData webTemplateAppData, LoginUserDTO loginUser) {
		ActivityModule activityModule = null;
		// 模块类型
		String type = webTemplateAppData.getType();
		ModuleTypeEnum moduleType = ModuleTypeEnum.fromValue(type);
		String appName = webTemplateAppData.getName();
		switch (moduleType) {
			case TPK:
				activityModule = activityModuleService.generateTpkModule(activityId, templateAppId, appName, loginUser);
				break;
			case STAR:
				activityModule = activityModuleService.generateStarModule(activityId, templateAppId, appName, loginUser);
				break;
			case WORK:
				activityModule = activityModuleService.generateWorkModule(activityId, templateAppId, appName, loginUser);
				break;
			case PUNCH:
				activityModule = activityModuleService.generatePunchModule(activityId, templateAppId, appName, loginUser);
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
				.coverUrl("http://p.ananas.chaoxing.com/star3/origin/" + activityModule.getIconCloudId())
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
			default:

		}
		return "";
	}

	/**更新活动状态
	 * @Description
	 * @author wwb
	 * @Date 2020-12-11 14:55:01
	 * @param activityId
	 * @param status
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityStatus(Integer activityId, Activity.StatusEnum status) {
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, status.getValue())
		);
		if (Objects.equals(Activity.StatusEnum.ENDED, status)) {
			// 当活动结束时触发用户合格判定
			activityInspectionResultDecideQueueService.push(activityId);
			// 触发黑名单判定
			blacklistAutoAddQueueService.push(new BlacklistAutoAddQueueService.QueueParamDTO(activityId));
		}
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

	/**同步活动websiteId
	 * @Description
	 * @author wwb
	 * @Date 2021-05-10 15:28:53
	 * @param activityId
	 * @return void
	 */
	public void syncActivityWebsiteId(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		if (activity != null) {
			Integer pageId = activity.getPageId();
			if (pageId != null) {
				Integer websiteId = mhApiService.getWebsiteIdByPageId(pageId);
				activityMapper.update(null, new UpdateWrapper<Activity>()
						.lambda()
						.eq(Activity::getId, activityId)
						.set(Activity::getWebsiteId, websiteId)
				);
			}
		}
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

}