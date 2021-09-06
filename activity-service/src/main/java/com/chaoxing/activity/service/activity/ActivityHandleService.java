package com.chaoxing.activity.service.activity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateDTO;
import com.chaoxing.activity.dto.activity.ActivityCreateParamDTO;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.chaoxing.activity.dto.activity.ActivityUpdateParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneParamDTO;
import com.chaoxing.activity.dto.manager.mh.MhCloneResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateParamDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignCreateResultDTO;
import com.chaoxing.activity.dto.manager.sign.create.SignUpCreateParamDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.mapper.ActivityDetailMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.WebTemplateService;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.service.activity.engine.ActivityComponentValueService;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.manager.ActivityManagerService;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.menu.ActivityMenuService;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.chaoxing.activity.service.activity.scope.ActivityClassService;
import com.chaoxing.activity.service.activity.scope.ActivityScopeService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.event.ActivityChangeEventService;
import com.chaoxing.activity.service.inspection.InspectionConfigHandleService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import com.chaoxing.activity.service.queue.activity.ActivityInspectionResultDecideQueueService;
import com.chaoxing.activity.service.queue.activity.ActivityWebsiteIdSyncQueueService;
import com.chaoxing.activity.service.queue.blacklist.BlacklistAutoAddQueueService;
import com.chaoxing.activity.util.ApplicationContextHolder;
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
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Arrays;
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
	private ActivityClassService activityClassService;
	@Resource
	private WebTemplateService webTemplateService;
	@Resource
	private ActivityStatusService activityStatusService;
	@Resource
	private ActivityChangeEventService activityChangeEventService;
	@Resource
	private ActivityManagerService activityManagerService;
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
	private ActivityMenuService activityMenuService;
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
	private ClassifyHandleService classifyHandleService;
	@Resource
	private WorkApiService workApiService;

	/**
	* @Description
	* @author huxiaolong
	* @Date 2021-09-03 18:27:01
	* @param activityCreateParamDto
	* @param signCreateParamDto
	* @param wfwRegionalArchitectureDtos
	* @param loginUser
	* @return java.lang.Integer
	*/
	@Transactional(rollbackFor = Exception.class)
	public Integer add(ActivityCreateParamDTO activityCreateParamDto, SignCreateParamDTO signCreateParamDto, List<WfwAreaDTO> wfwRegionalArchitectureDtos, LoginUserDTO loginUser) {
		return ApplicationContextHolder.getBean(ActivityHandleService.class).add(activityCreateParamDto, signCreateParamDto, wfwRegionalArchitectureDtos, null, loginUser);
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
	public Integer add(ActivityCreateParamDTO activityCreateParamDto, SignCreateParamDTO signCreateParamDto, List<WfwAreaDTO> wfwRegionalArchitectureDtos, List<Integer> releaseClassIds, LoginUserDTO loginUser) {
		Activity activity = activityCreateParamDto.buildActivity();
		// 新增活动输入验证
		activityValidationService.addInputValidate(activity);
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos) && CollectionUtils.isEmpty(releaseClassIds)) {
			throw new BusinessException("请选择发布范围");
		}
		// 添加报名签到
		SignCreateResultDTO signCreateResult = handleSign(activity, signCreateParamDto, loginUser);
		activity.setSignId(signCreateResult.getSignId());
		// 处理活动的状态, 新增的活动都是待发布的
		activity.beforeCreate(loginUser.getUid(), loginUser.getRealName(), loginUser.getFid(), loginUser.getOrgName());
		activityMapper.insert(activity);
		Integer activityId = activity.getId();
		// 保存活动报名的报名条件启用
		signUpConditionService.saveActivitySignUpEnables(activityId, activityCreateParamDto.getSucTemplateComponentIds());
		// 保存自定义组件值
		activityComponentValueService.saveActivityComponentValues(activityId, activityCreateParamDto.getActivityComponentValues());
		// 保存门户模板
		bindWebTemplate(activity, activityCreateParamDto.getWebTemplateId(), loginUser);

		inspectionConfigHandleService.initInspectionConfig(activityId);
		activityStatSummaryHandlerService.init(activityId);
		// 活动详情
		ActivityDetail activityDetail = activityCreateParamDto.buildActivityDetail(activityId);
		activityDetailMapper.insert(activityDetail);
		// 活动菜单配置
		activityMenuService.configActivityMenu(activityId, activityMenuService.listMenu().stream().map(ActivityMenuDTO::getValue).collect(Collectors.toList()));
		// 若活动由市场所建，新增活动市场与活动关联
		activityMarketService.add(activity);
		// 默认添加活动市场管理
		// 添加管理员
		ActivityManager activityManager = ActivityManager.buildCreator(activity);
		activityManagerService.add(activityManager, loginUser);
		// 处理发布范围
		if (CollectionUtils.isNotEmpty(releaseClassIds)) {
			activityClassService.batchAddOrUpdate(activityId, releaseClassIds);
		} else {
			activityScopeService.batchAdd(activityId, wfwRegionalArchitectureDtos);
		}
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
		return ApplicationContextHolder.getBean(ActivityHandleService.class).edit(activityUpdateParamDto, signCreateParam, wfwRegionalArchitectureDtos, null, loginUser);
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
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos)) {
			throw new BusinessException("请选择发布范围");
		}
		Integer activityId = activity.getId();
		String activityEditLockKey = getActivityEditLockKey(activityId);
		return distributedLock.lock(activityEditLockKey, () -> {
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
			// 更新报名签到
			Integer signId = existActivity.getSignId();
			signCreateParam.setId(signId);
			handleSign(activity, signCreateParam, loginUser);
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
			// 处理门户模版的绑定
			bindWebTemplate(existActivity, activity.getWebTemplateId(), loginUser);
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
			if (CollectionUtils.isNotEmpty(releaseClassIds)) {
				activityClassService.batchAddOrUpdate(activityId, releaseClassIds);
			} else {
				activityScopeService.batchAdd(activityId, wfwRegionalArchitectureDtos);
			}
			// 活动改变
			activityChangeEventService.dataChange(activity, existActivity, loginUser);
			return activity;
		}, e -> {
			log.error("更新活动:{} error:{}", JSON.toJSONString(activity), e.getMessage());
			throw new BusinessException("更新活动失败");
		});
	}

	/**更新活动基本信息
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-08-04 17:16:50
	 * @param activityUpdateParamDto
	 * @param wfwRegionalArchitectureDtos
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityBasicInfo(ActivityUpdateParamDTO activityUpdateParamDto, final List<WfwAreaDTO> wfwRegionalArchitectureDtos, LoginUserDTO loginUser) {
		Activity activity = activityUpdateParamDto.buildActivity();
		activityValidationService.updateInputValidate(activity);
		if (CollectionUtils.isEmpty(wfwRegionalArchitectureDtos)) {
			throw new BusinessException("请选择发布范围");
		}
		Integer activityId = activity.getId();
		String activityEditLockKey = getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
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
			// 更新自定义组件的值
			activityComponentValueService.updateActivityComponentValues(activityId, activityUpdateParamDto.getActivityComponentValues());
			ActivityDetail activityDetail = activityQueryService.getDetailByActivityId(activityId);
			if (activityDetail == null) {
				activityDetail = activityUpdateParamDto.buildActivityDetail();
				activityDetailMapper.insert(activityDetail);
			} else {
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
	public void updateSignUp(Integer activityId, List<Integer> sucTemplateComponentIds, SignCreateParamDTO signCreateParam, LoginUserDTO loginUser) {
		String activityEditLockKey = getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			Activity existActivity = activityValidationService.editAble(activityId, loginUser);
			// 更新报名签到
			signCreateParam.setId(existActivity.getSignId());
			handleSign(existActivity, signCreateParam, loginUser);
			// 更新
			signUpConditionService.updateActivitySignUpEnables(activityId, sucTemplateComponentIds);
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
	 * @param loginUser
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void release(Integer activityId, Integer marketId, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.activityExist(activityId);
		// 当非市场发布活动或当前市场的活动发布活动，均可修改活动状态
		if (marketId == null || Objects.equals(marketId, activity.getMarketId())) {
			activity = activityValidationService.releaseAble(activityId, loginUser);
			activity.release(loginUser.getUid());
			activityStatusService.updateReleaseStatus(activity);
		}
		// 修改活动-市场状态信息
		if (marketId != null) {
			activity.release(loginUser.getUid());
			activityMarketService.updateMarketActivityStatus(marketId, activity);
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
	public void cancelRelease(Integer activityId, Integer marketId, LoginUserDTO loginUser) {
		Activity activity = activityValidationService.activityExist(activityId);
		// 当非市场发布活动或当前市场的活动发布活动，均可修改活动状态
		if (marketId == null || Objects.equals(marketId, activity.getMarketId())) {
			activity = activityValidationService.cancelReleaseAble(activity, loginUser);
			activity.cancelRelease();
			activityStatusService.updateReleaseStatus(activity);
		}
		// 修改活动-市场状态信息
		if (marketId != null) {
			activity.cancelRelease();
			activityMarketService.updateMarketActivityStatus(marketId, activity);
		}
	}


	/**更新fid下所有market的中活动id为activityId的活动
	* @Description
	* @author huxiaolong
	* @Date 2021-08-12 17:42:15
	* @param activityId
	* @param fid
	* @param uid
	* @param released
	* @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void updateActivityReleaseStatus(Integer activityId, Integer fid, Integer uid, boolean released) {
		LoginUserDTO loginUser = LoginUserDTO.buildDefault(uid, "", fid, "");
		List<Integer> marketIdsUnderFid = marketQueryService.listMarketIdsByActivityIdFid(fid, activityId);
		if (released) {
			marketIdsUnderFid.forEach(marketId -> {
				release(activityId, marketId, loginUser);
			});
			return;
		}
		marketIdsUnderFid.forEach(marketId -> {
			cancelRelease(activityId, marketId, loginUser);
		});
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
	public void delete(Integer activityId, Integer marketId, LoginUserDTO loginUser) {
		Activity activity =  activityValidationService.activityExist(activityId);
		boolean isCreateMarket = Objects.equals(marketId, activity.getMarketId());
		// marketId为空 或者 当前marketId 和 活动marketId 一致时，进行活动真实删除，需要验证是否能删除；
		if (marketId == null || isCreateMarket) {
			// 验证是否能删除
			activity = activityValidationService.deleteAble(activityId, loginUser);
			activity.delete();
			activityMapper.update(null, new UpdateWrapper<Activity>()
					.lambda()
					.eq(Activity::getId, activityId)
					.set(Activity::getStatus, activity.getStatus())
			);
			// 活动状态改变
			activityChangeEventService.statusChange(activity);
		}
		// marketId不为空，删除活动-市场关联，isCreateMarket: true，则需要删除所有关联
		if (marketId != null) {
			activityMarketService.remove(activityId, marketId, isCreateMarket);
		}
	}

	/**删除fid下所有market的中活动id为activityId的活动
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
		List<Integer> marketIdsUnderFid = marketQueryService.listMarketIdsByActivityIdFid(fid, activityId);
		marketIdsUnderFid.forEach(marketId -> {
			delete(activityId, marketId, loginUser);
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
		createModuleByWebTemplateId(activityId, newWebTemplateId, loginUser);
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
				.set(Activity::getWebsiteId, null)
		);
		activityWebsiteIdSyncQueueService.add(activityId);
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

	/**
	* @Description
	* @author huxiaolong
	* @Date 2021-08-20 19:53:36
	* @param activityCreateDTO
	* @param loginUser
	* @return com.chaoxing.activity.model.Activity
	*/
	@Transactional(rollbackFor = Exception.class)
	public Activity newSharedActivity(ActivityCreateDTO activityCreateDTO, LoginUserDTO loginUser) {
		Integer createFid = activityCreateDTO.getFid();
		Integer createMarketId = activityCreateDTO.getMarketId();
		ActivityCreateParamDTO activityCreateParam = activityCreateDTO.getActivityInfo();
		activityCreateParam.setMarketId(createMarketId);
		// 判断是否开启报名、报名填报信息
		SignCreateParamDTO signCreateParam = SignCreateParamDTO.builder().name(activityCreateParam.getName()).build();
		if (activityCreateDTO.getOpenSignUp()) {
			SignUpCreateParamDTO signUpCreateParam = SignUpCreateParamDTO.buildDefault();
			signUpCreateParam.setFillInfo(activityCreateDTO.getOpenFillFormInfo());
			if (activityCreateDTO.getOpenFillFormInfo()) {
				signUpCreateParam.setFillInfoFormId(signApiService.createFormFillWithFields(activityCreateDTO.getFillFormInfo()));
			}
			signCreateParam.setSignUps(Lists.newArrayList(signUpCreateParam));
		}

		// todo 活动标识activityFlag propaganda_meeting 宣讲会是否需要设置
		Integer activityId = this.add(activityCreateParam, signCreateParam, wfwAreaApiService.listByFid(createFid), loginUser);
		// 若活动由市场所建，新增活动市场与活动关联，共享活动到其他机构
		List<Integer> shareFids = Optional.of(activityCreateDTO.getSharedFids()).filter(StringUtils::isNotBlank)
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
	public void updatePartialActivityInfo(ActivityCreateDTO activityCreateDTO, LoginUserDTO loginUser) {
		ActivityCreateParamDTO activityParam = activityCreateDTO.getActivityInfo();
		Activity activity = activityQueryService.getById(activityParam.getId());
		// 报名签到
		Integer signId = activity.getSignId();
		SignCreateParamDTO sign = SignCreateParamDTO.builder().build();
		if (signId != null) {
			sign = signApiService.getCreateById(signId);
		}
		ActivityUpdateParamDTO activityUpdateParam = ActivityUpdateParamDTO.buildActivityUpdateParam(activity, activityParam);
		this.edit(activityUpdateParam, sign, wfwAreaApiService.listByFid(activityCreateDTO.getFid()), loginUser);
	}

	/**
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-27 17:56:10
	* @param formId
	* @param formUserId
	* @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void deleteByOriginAndFormUserId(Integer formId, Integer formUserId) {
		if (formId == null || formUserId == null) {
			return;
		}
		Activity activity = activityQueryService.getActivityByOriginAndFormUserId(formId, formUserId);
		if (activity != null) {
			activity.delete();
			activityMapper.update(null, new UpdateWrapper<Activity>()
					.lambda()
					.eq(Activity::getId, activity.getId())
					.set(Activity::getStatus, activity.getStatus())
			);
			// 活动状态改变
			activityChangeEventService.statusChange(activity);

			// marketId不为空，删除活动-市场关联，isCreateMarket: true，则需要删除所有关联
			if (activity.getMarketId() != null) {
				activityMarketService.remove(activity.getId(), activity.getMarketId(), true);
			}
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
		// 重新设定活动的活动市场和模板id
		originActivity.setOriginActivityId(originActivity.getId());
		originActivity.setTemplateId(targetTemplate.getId());
		originActivity.setMarketId(marketId);
		// 处理克隆分类
		if (StringUtils.isNotBlank(originActivity.getActivityClassifyName())) {
			originActivity.setActivityClassifyId(classifyHandleService.getOrAddMarketClassify(marketId, originActivity.getActivityClassifyName()));
		}
		// 判断是否开启作品征集
		if (originActivity.getOpenWork()) {
			originActivity.setWorkId(workApiService.createDefault(uid, fid));
		}
		// 阅读默认关闭
		originActivity.setOpenReading(Boolean.FALSE);
		originActivity.setReadingId(null);
		originActivity.setReadingModuleId(null);
		ActivityCreateParamDTO targetActivity = ActivityCreateParamDTO.buildFromActivity(originActivity);
		// 报名签到
		SignCreateParamDTO signCreateParam = SignCreateParamDTO.builder().name(targetActivity.getName()).build();
		// 将克隆的活动保存
		ApplicationContextHolder.getBean(ActivityHandleService.class).add(targetActivity, signCreateParam, releaseScopes, loginUser);
	}
}