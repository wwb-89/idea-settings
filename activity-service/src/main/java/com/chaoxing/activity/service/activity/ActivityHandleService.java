package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgAddressDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.mapper.ActivityAreaFlagMapper;
import com.chaoxing.activity.mapper.ActivityDetailMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.mapper.ActivitySignModuleMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.event.ActivityChangeEventService;
import com.chaoxing.activity.service.inspection.InspectionConfigHandleService;
import com.chaoxing.activity.service.manager.GuanliApiService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueueService;
import com.chaoxing.activity.service.queue.activity.ActivityWebsiteIdSyncQueueService;
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
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
	private ActivityAreaFlagMapper activityAreaFlagMapper;
	@Resource
	private ActivitySignModuleMapper activitySignModuleMapper;

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityModuleService activityModuleService;
	@Resource
	private ActivityScopeService activityScopeService;
	@Resource
	private GuanliApiService guanliApiService;
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
	private SignApiService signApiService;
	@Resource
	private MhApiService mhApiService;
	@Resource
	private DistributedLock distributedLock;

	/**新增活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 15:54:16
	 * @param activity
	 * @param signCreateParam
	 * @param wfwRegionalArchitectures
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(Activity activity, SignCreateParamDTO signCreateParam, List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures, LoginUserDTO loginUser) {
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		// 处理活动类型
		handleActivityType(activity);
		// 添加报名签到
		SignCreateResultDTO signCreateResult = handleSign(activity, signCreateParam, loginUser);
		activity.setSignId(signCreateResult.getSignId());
		// 添加作品征集
		handleWork(activity, loginUser);
		// 处理活动的状态, 新增的活动都是待发布的
		activity.setStatus(Activity.StatusEnum.WAIT_RELEASE.getValue());
		activity.setReleased(false);
		activity.setCreateUid(loginUser.getUid());
		activity.setCreateUserName(loginUser.getRealName());
		activity.setCreateFid(loginUser.getFid());
		activity.setCreateOrgName(loginUser.getOrgName());
		activity.setStartDate(activity.getStartTime().toLocalDate());
		activity.setEndDate(activity.getEndTime().toLocalDate());
		String originType = activity.getOriginType();
		if (StringUtils.isBlank(originType)) {
			activity.setOriginType(Activity.OriginTypeEnum.NORMAL.getValue());
		}
		activityMapper.insert(activity);
		Integer activityId = activity.getId();
		inspectionConfigHandleService.initInspectionConfig(activityId);
		activityStatSummaryHandlerService.init(activityId);
		ActivityDetail activityDetail = ActivityDetail.builder()
				.activityId(activityId)
				.introduction(activity.getIntroduction())
				.build();
		activityDetailMapper.insert(activityDetail);

		// 添加管理员
		ActivityManager activityManager = new ActivityManager();
		activityManager.setActivityId(activity.getId());
		activityManager.setUid(activity.getCreateUid());
		activityManager.setUserName(activity.getCreateUserName());
		activityManager.setCreateUid(activity.getCreateUid());
		activityManagerService.add(activityManager, loginUser);
		// 处理发布范围
		if (CollectionUtils.isEmpty(wfwRegionalArchitectures)) {
			throw new BusinessException("请选择发布范围");
		}
		List<ActivityScope> activityScopes = WfwRegionalArchitectureDTO.convert2ActivityScopes(activityId, wfwRegionalArchitectures);
		// 新增发布范围
		activityScopeService.batchAdd(activityScopes);
		// 处理活动的所属区域
		handleActivityArea(activity, loginUser);
		// 活动改变
		activityChangeEventService.dataChange(activity, null, activity.getIntegral(), loginUser);
	}

	/**处理活动类型
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-10 14:35:41
	 * @param activity
	 * @return void
	*/
	private void handleActivityType(Activity activity) {
		String activityType = activity.getActivityType();
		Activity.ActivityTypeEnum activityTypeEnum = Activity.ActivityTypeEnum.fromValue(activityType);
		if (Activity.ActivityTypeEnum.ONLINE.equals(activityTypeEnum)) {
			activity.setAddress(null);
			activity.setLongitude(null);
			activity.setDimension(null);
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
		Integer signId = signCreateParam.getId();
		List<SignUpCreateParamDTO> signUps = signCreateParam.getSignUps();
		if (CollectionUtils.isNotEmpty(signUps)) {
			String activityFlag = activity.getActivityFlag();
			if (StringUtils.isEmpty(activityFlag)) {
				activityFlag = Activity.ActivityFlagEnum.NORMAL.getValue();
			}
		}
		if (signId == null) {
			// 签到的名称为活动引擎活动的名称
			signCreateParam.setName(activity.getName());
			// 新增报名签到
			signCreateParam.setUid(loginUser.getUid());
			signCreateParam.setUserName(loginUser.getRealName());
			signCreateParam.setFid(loginUser.getFid());
			signCreateParam.setOrgName(loginUser.getOrgName());
			return signApiService.create(signCreateParam);
		} else {
			// 修改报名签到
			signCreateParam.setName(activity.getName());
			return signApiService.update(signCreateParam);
		}
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
		Boolean openWork = activity.getOpenWork();
		openWork = Optional.ofNullable(openWork).orElse(Boolean.FALSE);
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

	/**处理活动所属范围
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 15:46:43
	 * @param activity
	 * @param loginUser
	 * @return void
	*/
	private void handleActivityArea(Activity activity, LoginUserDTO loginUser) {
		Integer fid = loginUser.getFid();
		try {
			OrgAddressDTO orgAddress = guanliApiService.getAddressByFid(fid);
			if (orgAddress == null) {
				return;
			}
			String province = orgAddress.getProvince();
			String city = orgAddress.getCity();
			String county = orgAddress.getCounty();
			// 删除活动下的区域标签
			Integer activityId = activity.getId();
			activityAreaFlagMapper.delete(new UpdateWrapper<ActivityAreaFlag>()
				.lambda()
					.eq(ActivityAreaFlag::getActivityId, activityId)
			);
			if (StringUtils.isNotEmpty(province)) {
				ActivityAreaFlag activityAreaFlag = generateActivityAreaFlag(activityId, province, ActivityAreaLevelEnum.PROVINCE);
				activityAreaFlagMapper.insert(activityAreaFlag);
			}
			if (StringUtils.isNotEmpty(city)) {
				ActivityAreaFlag activityAreaFlag = generateActivityAreaFlag(activityId, city, ActivityAreaLevelEnum.CITY);
				activityAreaFlagMapper.insert(activityAreaFlag);
			}
			if (StringUtils.isNotEmpty(county)) {
				ActivityAreaFlag activityAreaFlag = generateActivityAreaFlag(activityId, county, ActivityAreaLevelEnum.COUNTRY);
				activityAreaFlagMapper.insert(activityAreaFlag);
			}
		} catch (Exception e) {
			// 不影响活动的创建
			log.error("根据fid:{}获取区域信息error:{}", fid, e.getMessage());
			e.printStackTrace();
		}
	}

	private ActivityAreaFlag generateActivityAreaFlag(Integer activityId, String name, ActivityAreaLevelEnum activityAreaLevel) {
		ActivityAreaFlag activityAreaFlag = ActivityAreaFlag.builder()
				.activityId(activityId)
				.area(name)
				.areaLevel(activityAreaLevel.getValue())
				.build();
		return activityAreaFlag;
	}

	/**修改活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-11 15:41:49
	 * @param activity
	 * @param wfwRegionalArchitectures
	 * @param signCreateParam
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void edit(Activity activity, SignCreateParamDTO signCreateParam, final List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures, LoginUserDTO loginUser) {
		Integer activityId = activity.getId();
		String activityEditLockKey = getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			activityValidationService.addInputValidate(activity);
			// 处理活动类型
			handleActivityType(activity);
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
			// 克隆
			Activity oldActivity = new Activity();
			BeanUtils.copyProperties(existActivity, oldActivity);
			BigDecimal oldIntegralValue = existActivity.getIntegral();
			// 更新报名签到
			Integer signId = existActivity.getSignId();
			signCreateParam.setId(signId);
			SignCreateResultDTO signCreateResult = handleSign(activity, signCreateParam, loginUser);
			// 征集相关
			handleWork(activity, loginUser);
			// 处理活动相关
			LocalDateTime startTime = activity.getStartTime();
			LocalDateTime endTime = activity.getEndTime();

			String oldCoverCloudId = existActivity.getCoverCloudId();
			String newCoverCloudId = activity.getCoverCloudId();

			existActivity.setName(activity.getName());
			existActivity.setStartTime(startTime);
			existActivity.setEndTime(endTime);
			existActivity.setStartDate(startTime.toLocalDate());
			existActivity.setEndDate(endTime.toLocalDate());
			existActivity.setCoverCloudId(newCoverCloudId);
			existActivity.setCoverUrl(activity.getCoverUrl());
			existActivity.setOrganisers(activity.getOrganisers());
			existActivity.setActivityType(activity.getActivityType());
			existActivity.setAddress(activity.getAddress());
			existActivity.setDetailAddress(activity.getDetailAddress());
			existActivity.setLongitude(activity.getLongitude());
			existActivity.setDimension(activity.getDimension());
			existActivity.setActivityClassifyId(activity.getActivityClassifyId());
			existActivity.setPeriod(activity.getPeriod());
			existActivity.setCredit(activity.getCredit());
			existActivity.setSignId(activity.getSignId());
			existActivity.setWebTemplateId(activity.getWebTemplateId());
			existActivity.setTags(activity.getTags());
			existActivity.setIntegral(activity.getIntegral());
			existActivity.setOpenRating(activity.getOpenRating());
			existActivity.setRatingNeedAudit(activity.getRatingNeedAudit());
			existActivity.setOpenWork(activity.getOpenWork());
			existActivity.setWorkId(activity.getWorkId());
			existActivity.setTimingRelease(activity.getTimingRelease());
			existActivity.setTimingReleaseTime(activity.getTimingReleaseTime());
			existActivity.setTimeLengthUpperLimit(activity.getTimeLengthUpperLimit());
			activityMapper.update(existActivity, new UpdateWrapper<Activity>()
					.lambda()
					.eq(Activity::getId, activity.getId())
					.set(Activity::getTimingReleaseTime, existActivity.getTimingReleaseTime())
					.set(Activity::getTimeLengthUpperLimit, existActivity.getTimeLengthUpperLimit())
					.set(Activity::getIntegral, existActivity.getIntegral())
			);
			// 更新活动状态
			activityStatusService.statusUpdate(existActivity);
			ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activityId);
			if (activityDetail == null) {
				activityDetail = ActivityDetail.builder()
						.activityId(activityId)
						.introduction(activity.getIntroduction())
						.build();
				activityDetailMapper.insert(activityDetail);
			}else {
				activityDetailMapper.update(null, new UpdateWrapper<ActivityDetail>()
					.lambda()
						.eq(ActivityDetail::getId, activityDetail.getId())
						.set(ActivityDetail::getIntroduction, activity.getIntroduction())
				);
			}
			if (!Objects.equals(oldCoverCloudId, newCoverCloudId)) {
				// 清空封面url
				existActivity.setCoverUrl("");
			}
			// 处理发布范围
			if (CollectionUtils.isEmpty(wfwRegionalArchitectures)) {
				throw new BusinessException("请选择发布范围");
			}
			List<ActivityScope> activityScopes = WfwRegionalArchitectureDTO.convert2ActivityScopes(activityId, wfwRegionalArchitectures);
			// 删除以前发布的发布范围
			activityScopeService.deleteByActivityId(activityId);
			// 新增活动发布范围
			activityScopeService.batchAdd(activityScopes);
			// 活动改变
			activityChangeEventService.dataChange(activity, oldActivity, oldIntegralValue, loginUser);
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
	public void updateActivityStatus(Integer activityId, Activity.StatusEnum status) {
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, status.getValue())
		);
		if (Objects.equals(Activity.StatusEnum.ENDED, status)) {
			// 当活动结束时触发用户合格判定
			activityInspectionResultDecideQueueService.push(activityId);
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

}