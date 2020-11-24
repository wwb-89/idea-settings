package com.chaoxing.activity.service.activity;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.OrgAddressDTO;
import com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO;
import com.chaoxing.activity.dto.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.module.SignFormDTO;
import com.chaoxing.activity.mapper.ActivityAreaFlagMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.manager.GuanliApiService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.WfwRegionalArchitectureApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.TpkApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.util.enums.*;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
	private ActivityAreaFlagMapper activityAreaFlagMapper;

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private ActivityModuleService activityModuleService;
	@Resource
	private WfwRegionalArchitectureApiService wfwRegionalArchitectureApiService;
	@Resource
	private ActivityScopeService activityScopeService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private GuanliApiService guanliApiService;
	@Resource
	private TpkApiService tpkApiService;
	@Resource
	private WebTemplateService webTemplateService;

	@Resource
	private SignApiService signApiService;
	@Resource
	private MhApiService mhApiService;

	/**新增活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 15:54:16
	 * @param activity
	 * @param signForm
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(Activity activity, SignFormDTO signForm, LoginUserDTO loginUser, HttpServletRequest request) {
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		// 是否开启参与设置
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 添加报名签到
			Integer signId = handleSign(signForm, loginUser, request);
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
		activityMapper.insert(activity);
		// 处理活动的所属区域
		handleActivityArea(activity, loginUser);
//		Integer activityId = activity.getId();
		// 处理模块
//		handleActivityModules(activityId, activityModules);
	}

	/**处理报名签到
	 * @Description
	 * @author wwb
	 * @Date 2020-11-13 15:46:49
	 * @param signForm
	 * @param loginUser
	 * @param request
	 * @return java.lang.Integer
	*/
	private Integer handleSign(SignFormDTO signForm, LoginUserDTO loginUser, HttpServletRequest request) {
		Integer signId = signForm.getId();
		if (signId == null) {
			// 新增报名签到
			signForm.setCreateUid(loginUser.getUid());
			signForm.setCreateUserName(loginUser.getRealName());
			signForm.setCreateFid(loginUser.getFid());
			signForm.setCreateOrgName(loginUser.getOrgName());
			signId = signApiService.create(signForm, request);
		} else {
			// 修改报名签到
			signApiService.update(signForm, request);
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
	 * @param signForm
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void edit(Activity activity, SignFormDTO signForm, LoginUserDTO loginUser, HttpServletRequest request) {
		activityValidationService.addInputValidate(activity);
		Integer activityId = activity.getId();
		Activity existActivity = activityValidationService.editAble(activityId, loginUser);
		// 更新报名签到
		Boolean enableSign = activity.getEnableSign();
		if (enableSign) {
			// 修改或更新
			Integer signId = existActivity.getSignId();
			signForm.setId(signId);
			handleSign(signForm, loginUser, request);
		}
		// 处理活动相关
		LocalDate startDate = activity.getStartDate();
		LocalDate endDate = activity.getEndDate();

		existActivity.setName(activity.getName());
		existActivity.setStartDate(startDate);
		existActivity.setEndDate(endDate);
		existActivity.setCoverCloudId(activity.getCoverCloudId());
		existActivity.setActivityType(activity.getActivityType());
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
				.eq(Activity::getId, activity.getId())
		);
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

	/**发布活动
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 15:41:48
	 * @param activityId
	 * @param selectedFids
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void release(Integer activityId, List<Integer> selectedFids, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.releaseAble(activityId, loginUser);
		// 发布活动
		activity.setReleased(true);
		activity.setReleaseTime(LocalDateTime.now());
		activity.setReleaseUid(loginUser.getUid());
		Integer status = calActivityStatus(activity.getStartDate(), activity.getEndDate(), Activity.StatusEnum.RELEASED.getValue());
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activity.getId())
				.set(Activity::getReleased, true)
				.set(Activity::getReleaseTime, LocalDateTime.now())
				.set(Activity::getReleaseUid, loginUser.getUid())
				.set(Activity::getStatus, status)
		);
		// 处理参与范围
		handleReleaseScope(activityId, selectedFids, loginUser);
		// 通知模块方刷新参与范围缓存
		// 查询活动的作品征集模块活动id列表
		List<String> externalIds = activityModuleService.listExternalIdsByActivityIdAndType(activityId, ModuleTypeEnum.WORK.getValue());
		if (CollectionUtils.isNotEmpty(externalIds)) {
			workApiService.clearActivityParticipateScopeCache(externalIds.stream().map(v -> Integer.parseInt(v)).collect(Collectors.toList()));
		}
	}

	private void handleReleaseScope(Integer activityId, List<Integer> selectedFids, LoginUserDTO loginUser) {
		Integer fid = loginUser.getFid();
		List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures = wfwRegionalArchitectureApiService.listByFid(fid);
		List<WfwRegionalArchitectureDTO> selectedWfwRegionalArchitectures = new ArrayList<>();
		List<ActivityScope> activityScopes = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			if (CollectionUtils.isEmpty(selectedFids)) {
				// 有范围但是没有指定范围
				throw new BusinessException("请指定发布范围");
			}
			for (WfwRegionalArchitectureDTO wfwRegionalArchitecture : wfwRegionalArchitectures) {
				if (selectedFids.contains(wfwRegionalArchitecture.getFid())) {
					selectedWfwRegionalArchitectures.add(wfwRegionalArchitecture);
				}
			}
			activityScopes = convert2(activityId, selectedWfwRegionalArchitectures);
		} else {
			activityScopes.add(ActivityScope.structure(activityId, loginUser.getFid(), loginUser.getOrgName()));
		}
		// 删除以前发布的参与范围
		activityScopeService.deleteByActivityId(activityId);
		// 新增参与范围
		activityScopeService.batchAdd(activityScopes);
	}

	private List<ActivityScope> convert2(Integer activityId, List<WfwRegionalArchitectureDTO> wfwRegionalArchitectures) {
		List<ActivityScope> activityScopes = new ArrayList<>();
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			for (WfwRegionalArchitectureDTO wfwRegionalArchitecture : wfwRegionalArchitectures) {
				ActivityScope activityScope = ActivityScope.builder()
						.activityId(activityId)
						.hierarchyId(wfwRegionalArchitecture.getId())
						.name(wfwRegionalArchitecture.getName())
						.hierarchyPid(wfwRegionalArchitecture.getPid())
						.code(wfwRegionalArchitecture.getCode())
						.links(wfwRegionalArchitecture.getLinks())
						.level(wfwRegionalArchitecture.getLevel())
						.adjustedLevel(wfwRegionalArchitecture.getLevel())
						.fid(wfwRegionalArchitecture.getFid())
						.existChild(Optional.ofNullable(wfwRegionalArchitecture.getExistChild()).orElse(Boolean.FALSE))
						.sort(wfwRegionalArchitecture.getSort())
						.build();
				activityScopes.add(activityScope);
			}
		}
		return activityScopes;
	}

	/**取消发布（下架）
	 * @Description
	 * @author wwb
	 * @Date 2020-11-12 17:23:58
	 * @param activityId
	 * @param loginUser
	 * @return void
	*/
	public void cancelRelease(Integer activityId, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.cancelReleaseAble(activityId, loginUser);
		Integer status = calActivityStatus(activity.getStartDate(), activity.getEndDate(), Activity.StatusEnum.WAIT_RELEASE.getValue());
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activity.getId())
				.set(Activity::getReleased, false)
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

	/**更新发布范围
	 * @Description
	 * @author wwb
	 * @Date 2020-11-20 11:09:31
	 * @param activityId
	 * @param selectedFids
	 * @param loginUser
	 * @return void
	*/
	public void updateReleaseScope(Integer activityId, List<Integer> selectedFids, LoginUserDTO loginUser) {
		activityValidationService.updateReleaseAble(activityId, loginUser);
		// 处理参与范围
		handleReleaseScope(activityId, selectedFids, loginUser);
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
	public void delete(Integer activityId, LoginUserDTO loginUser) {
		// 验证是否能删除
		activityValidationService.deleteAble(activityId, loginUser);
		activityMapper.update(null, new UpdateWrapper<Activity>()
			.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getStatus, Activity.StatusEnum.DELETED.getValue())
		);
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
	 * @return void
	*/
	@Transactional
	public void bindWebTemplate(Integer activityId, Integer webTemplateId, LoginUserDTO loginUser) {
		// 创建模块
		createModuleByWebTemplateId(activityId, webTemplateId, loginUser);
		// 克隆
		MhCloneParamDTO mhCloneParam = packageMhCloneParam(activityId, webTemplateId, loginUser);
		Integer pageId = mhApiService.cloneTemplate(mhCloneParam);
		activityMapper.update(null, new UpdateWrapper<Activity>()
				.lambda()
				.eq(Activity::getId, activityId)
				.set(Activity::getWebTemplateId, webTemplateId)
				.set(Activity::getPageId, pageId)
		);
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

	private MhCloneParamDTO packageMhCloneParam(Integer activityId, Integer webTemplateId, LoginUserDTO loginUser) {
		WebTemplate webTemplate = webTemplateService.webTemplateExist(webTemplateId);
		MhCloneParamDTO mhCloneParam = new MhCloneParamDTO();
		mhCloneParam.setTemplateId(webTemplate.getTemplateId());
		mhCloneParam.setUid(loginUser.getUid());
		mhCloneParam.setWfwfid(loginUser.getFid());
		List<MhCloneParamDTO.MhAppDTO> appList = packageTemplateApps(activityId, webTemplateId);
		mhCloneParam.setAppList(appList);
		return mhCloneParam;
	}

	private List<MhCloneParamDTO.MhAppDTO> packageTemplateApps(Integer activityId, Integer webTemplateId) {
		List<MhCloneParamDTO.MhAppDTO> result = new ArrayList<>();
		List<WebTemplateApp> webTemplateApps = webTemplateService.listByWebTemplateId(webTemplateId);
		if (CollectionUtils.isEmpty(webTemplateApps)) {
			return result;
		}
		for (WebTemplateApp webTemplateApp : webTemplateApps) {
			MhCloneParamDTO.MhAppDTO mhApp = packageMhAppDTO(webTemplateApp, activityId);
			result.add(mhApp);
		}
		return result;
	}

	private MhCloneParamDTO.MhAppDTO packageMhAppDTO(WebTemplateApp webTemplateApp, Integer activityId) {
		MhCloneParamDTO.MhAppDTO mhApp = new MhCloneParamDTO.MhAppDTO();
		mhApp.setAppName(webTemplateApp.getAppName());
		Integer dataSourceType = webTemplateApp.getDataSourceType();
		MhAppDataSourceEnum mhAppDataSource = MhAppDataSourceEnum.fromValue(dataSourceType);
		mhApp.setDataType(mhAppDataSource.getValue());
		if (mhAppDataSource.equals(MhAppDataSourceEnum.LOCAL)) {
			// 本地数据源
			List<MhCloneParamDTO.MhAppDataDTO> mhAppDatas = packageMhAppData(webTemplateApp, activityId);
			mhApp.setDataList(mhAppDatas);
		} else {
			String dataUrl = packageMhAppDataUrl(webTemplateApp);
			mhApp.setDataUrl(dataUrl);
		}
		return mhApp;
	}

	private List<MhCloneParamDTO.MhAppDataDTO> packageMhAppData(WebTemplateApp webTemplateApp, Integer activityId) {
		// 目前只处理图标
		return null;
	}

	private String packageMhAppDataUrl(WebTemplateApp webTemplateApp) {
		String dataType = webTemplateApp.getDataType();
		MhAppDataTypeEnum mhAppDataType = MhAppDataTypeEnum.fromValue(dataType);
		switch (mhAppDataType) {
			case ACTIVITY_COVER:
				break;
			case ACTIVITY_INFO:
				break;
			case SIGN_IN_UP:
				break;
			case ACTIVITY_MODULE:
				break;
			case ACTIVITY_MAP:
				break;
			case ACTIVITY_LIST:
				break;
			default:

		}
		return "";
	}

}