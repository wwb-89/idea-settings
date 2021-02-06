package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgAddressDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.module.SignAddEditDTO;
import com.chaoxing.activity.mapper.ActivityAreaFlagMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.form.ActivityFormRecordService;
import com.chaoxing.activity.service.manager.GuanliApiService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.util.constant.ActivityMhUrlConstant;
import com.chaoxing.activity.util.constant.ActivityModuleConstant;
import com.chaoxing.activity.util.enums.*;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
	private ActivityAreaFlagMapper activityAreaFlagMapper;

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityModuleService activityModuleService;
	@Resource
	private ActivityScopeService activityScopeService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private GuanliApiService guanliApiService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private ActivityStatusHandleService activityStatusHandleService;
	@Resource
	private ActivityCoverService activityCoverService;
	@Resource
	private ActivityStartNoticeHandleService activityStartNoticeHandleService;
	@Resource
	private ActivityFormRecordService activityFormRecordService;

	@Resource
	private SignApiService signApiService;
	@Resource
	private MhApiService mhApiService;

	/**新增活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 15:54:16
	 * @param activity
	 * @param signAddEdit
	 * @param wfwRegionalArchitectures
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(Activity activity, SignAddEditDTO signAddEdit, List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures, LoginUserDTO loginUser, HttpServletRequest request) {
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		// 处理活动类型
		handleActivityType(activity);
		// 是否开启参与设置
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 添加报名签到
			Integer signId = handleSign(activity, signAddEdit, loginUser, request);
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
		activity.setCreateUserName(loginUser.getRealName());
		activity.setCreateFid(loginUser.getFid());
		activity.setCreateOrgName(loginUser.getOrgName());
		activity.setStartDate(activity.getStartTime().toLocalDate());
		activity.setEndDate(activity.getEndTime().toLocalDate());
		activityMapper.insert(activity);
		activityCoverService.noticeUpdateCoverUrl(activity.getId(), activity.getCoverCloudId());
		// 活动参与范围
		Integer activityId = activity.getId();
		if (CollectionUtils.isEmpty(wfwRegionalArchitectures)) {
			throw new BusinessException("请选择参与范围");
		}
		List<ActivityScope> activityScopes = WfwRegionalArchitectureDTO.convert2ActivityScopes(activityId, wfwRegionalArchitectures);
		// 新增参与范围
		activityScopeService.batchAdd(activityScopes);

		// 处理活动的所属区域
		handleActivityArea(activity, loginUser);
		// 订阅活动状态处理
		activityStatusHandleService.subscibeStatusUpdate(activity.getId(), activity.getStartTime(), activity.getEndTime());
		// 订阅活动通知发送
		activityStartNoticeHandleService.subscibeActivityNotice(activityId, activity.getStartTime());
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
		ActivityTypeEnum activityTypeEnum = ActivityTypeEnum.fromValue(activityType);
		if (ActivityTypeEnum.ONLINE.equals(activityTypeEnum)) {
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
	 * @param signAddEdit
	 * @param loginUser
	 * @param request
	 * @return java.lang.Integer
	*/
	private Integer handleSign(Activity activity, SignAddEditDTO signAddEdit, LoginUserDTO loginUser, HttpServletRequest request) {
		Integer signId = signAddEdit.getId();
		if (signId == null) {
			// 签到的名称为活动引擎活动的名称
			signAddEdit.setName(activity.getName());
			// 新增报名签到
			signAddEdit.setCreateUid(loginUser.getUid());
			signAddEdit.setCreateUserName(loginUser.getRealName());
			signAddEdit.setCreateFid(loginUser.getFid());
			signAddEdit.setCreateOrgName(loginUser.getOrgName());
			signAddEdit.setUpdateUid(loginUser.getUid());
			signId = signApiService.create(signAddEdit, request);
		} else {
			// 修改报名签到
			signAddEdit.setName(activity.getName());
			signApiService.update(signAddEdit, request);
		}
		return signId;
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
	 * @param signAddEdit
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void edit(Activity activity, SignAddEditDTO signAddEdit, List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures, LoginUserDTO loginUser, HttpServletRequest request) {
		activityValidationService.addInputValidate(activity);
		// 处理活动类型
		handleActivityType(activity);
		Integer activityId = activity.getId();
		Activity existActivity = activityValidationService.editAble(activityId, loginUser);
		// 更新报名签到
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 修改或更新
			Integer signId = existActivity.getSignId();
			signAddEdit.setId(signId);
			signId = handleSign(activity, signAddEdit, loginUser, request);
			activity.setSignId(signId);
		}
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
		existActivity.setEnableSign(activity.getEnableSign());
		existActivity.setSignId(activity.getSignId());
		existActivity.setWebTemplateId(activity.getWebTemplateId());
		existActivity.setTags(activity.getTags());
		// 根据活动时间判断状态
		Integer status = activityStatusHandleService.calActivityStatus(existActivity);
		existActivity.setStatus(status);
		activityMapper.update(existActivity, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activity.getId())
		);
		if (!Objects.equals(oldCoverCloudId, newCoverCloudId)) {
			// 清空封面url
			existActivity.setCoverUrl("");
			activityCoverService.noticeUpdateCoverUrl(activityId, newCoverCloudId);
		}
		// 活动参与范围
		if (CollectionUtils.isEmpty(wfwRegionalArchitectures)) {
			throw new BusinessException("请选择参与范围");
		}
		List<ActivityScope> activityScopes = WfwRegionalArchitectureDTO.convert2ActivityScopes(activityId, wfwRegionalArchitectures);
		// 删除以前发布的参与范围
		activityScopeService.deleteByActivityId(activityId);
		// 新增参与范围
		activityScopeService.batchAdd(activityScopes);
		// 订阅活动状态处理
		activityStatusHandleService.subscibeStatusUpdate(activity.getId(), activity.getStartTime(), activity.getEndTime());
		// 订阅活动通知发送
		activityStartNoticeHandleService.subscibeActivityNotice(activityId, activity.getStartTime());
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
		// 发布活动
		activity.setReleased(true);
		activity.setReleaseTime(LocalDateTime.now());
		activity.setReleaseUid(loginUser.getUid());
		Integer status = activityStatusHandleService.calActivityStatus(activity);
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activity.getId())
				.set(Activity::getReleased, true)
				.set(Activity::getReleaseTime, LocalDateTime.now())
				.set(Activity::getReleaseUid, loginUser.getUid())
				.set(Activity::getStatus, status)
		);
		// 新增表单记录
		activityFormRecordService.add(activity);
		// 通知模块方刷新参与范围缓存
		// 查询活动的作品征集模块活动id列表
		List<String> externalIds = activityModuleService.listExternalIdsByActivityIdAndType(activityId, ModuleTypeEnum.WORK.getValue());
		if (CollectionUtils.isNotEmpty(externalIds)) {
			workApiService.clearActivityParticipateScopeCache(externalIds.stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList()));
		}
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
		activity.setReleased(Boolean.FALSE);
		Integer status = activityStatusHandleService.calActivityStatus(activity);
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activity.getId())
				.set(Activity::getReleased, activity.getReleased())
				.set(Activity::getStatus, status)
		);
		// 删除活动范围
		activityScopeService.deleteByActivityId(activityId);
		// 通知模块方刷新参与范围缓存
		// 查询活动的作品征集模块活动id列表
		List<String> externalIds = activityModuleService.listExternalIdsByActivityIdAndType(activityId, ModuleTypeEnum.WORK.getValue());
		if (CollectionUtils.isNotEmpty(externalIds)) {
			workApiService.clearActivityParticipateScopeCache(externalIds.stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList()));
		}
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
		activityValidationService.deleteAble(activityId, loginUser);
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
		);
		activityStartNoticeHandleService.cancelSubscibeActivityNotice(activityId);
		// 删除表单记录
		activityFormRecordService.delete(activityId);
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
	@Transactional(rollbackFor = Exception.class)
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
		List<ActivityModule> activityModules = new ArrayList<>();
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
	private MhCloneParamDTO packageMhCloneParam(Activity activity, Integer webTemplateId, LoginUserDTO loginUser) {
		WebTemplate webTemplate = webTemplateService.webTemplateExist(webTemplateId);
		MhCloneParamDTO mhCloneParam = new MhCloneParamDTO();
		mhCloneParam.setTemplateId(webTemplate.getTemplateId());
		mhCloneParam.setWebsiteName(activity.getName());
		mhCloneParam.setOriginPageId(webTemplate.getOriginPageId());
		mhCloneParam.setUid(loginUser.getUid());
		mhCloneParam.setWfwfid(loginUser.getFid());
		List<MhCloneParamDTO.MhAppDTO> appList = packageTemplateApps(activity, webTemplateId);
		mhCloneParam.setAppList(appList);
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
		List<MhCloneParamDTO.MhAppDTO> result = new ArrayList<>();
		List<WebTemplateApp> webTemplateApps = webTemplateService.listAppByWebTemplateId(webTemplateId);
		if (CollectionUtils.isEmpty(webTemplateApps)) {
			return result;
		}
		for (WebTemplateApp webTemplateApp : webTemplateApps) {
			MhCloneParamDTO.MhAppDTO mhApp = packageMhAppDTO(activity, webTemplateApp);
			result.add(mhApp);
		}
		return result;
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
			List<MhCloneParamDTO.MhAppDataDTO> mhAppDatas = packageMhAppData(webTemplateApp, activity);
			mhApp.setDataList(mhAppDatas);
		} else {
			String dataUrl = packageMhAppDataUrl(activity, webTemplateApp);
			mhApp.setDataUrl(dataUrl);
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
		List<MhCloneParamDTO.MhAppDataDTO> result = new ArrayList<>();
		// 目前只处理图标
		Integer appId = webTemplateApp.getAppId();
		List<ActivityModule> activityModules = activityModuleService.listByActivityIdAndTemplateId(activityId, appId);
		if (CollectionUtils.isNotEmpty(activityModules)) {
			for (ActivityModule activityModule : activityModules) {
				String accessUrl = String.format(ActivityModuleConstant.MODULE_ACCESS_URL, activityModule.getType(), activityModule.getExternalId());
				MhCloneParamDTO.MhAppDataDTO mhAppData = MhCloneParamDTO.MhAppDataDTO.builder()
						.title(activityModule.getName())
						// 访问的url
						.url(accessUrl)
						.pageType(3)
						.coverUrl("http://p.ananas.chaoxing.com/star3/origin/" + activityModule.getIconCloudId())
						.build();
				result.add(mhAppData);
			}
		}
		return result;
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
			case SIGN_IN_UP:
				return String.format(ActivityMhUrlConstant.ACTIVITY_SIGN_URL, activityId);
			case ACTIVITY_LIST:
				return String.format(ActivityMhUrlConstant.ACTIVITY_RECOMMEND_URL, activityId);
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
	public void updateActivityStatus(Integer activityId, Integer status) {
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, status)
		);
	}

}