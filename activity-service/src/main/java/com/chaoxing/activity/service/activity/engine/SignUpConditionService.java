package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.blacklist.UserBlacklistResultDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.dto.verification.UserSignUpAbleVerificationDTO;
import com.chaoxing.activity.mapper.ActivitySignUpConditionMapper;
import com.chaoxing.activity.mapper.SignUpConditionEnableMapper;
import com.chaoxing.activity.mapper.SignUpConditionMapper;
import com.chaoxing.activity.mapper.TemplateSignUpConditionMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.market.MarketSignupConfigService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.ApplicationContextHolder;
import com.chaoxing.activity.util.WfwFormUtils;
import com.chaoxing.activity.util.enums.ConditionEnum;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**报名条件服务
 * @author wwb
 * @version ver 1.0
 * @className SignUpConditionService
 * @description
 * @blame wwb
 * @date 2021-07-09 17:28:30
 */
@Slf4j
@Service
public class SignUpConditionService {

	@Resource
	private SignUpConditionMapper signUpConditionMapper;
	@Resource
	private SignUpConditionEnableMapper signUpConditionEnableMapper;
	@Resource
	private TemplateSignUpConditionMapper templateSignUpConditionMapper;
	@Resource
	private ActivitySignUpConditionMapper activitySignUpConditionMapper;
	@Resource
	private WfwFormApiService wfwFormApiService;
	@Resource
	private TemplateComponentService templateComponentService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private MarketSignupConfigService marketSignupConfigService;
	@Resource
	private BlacklistQueryService blacklistQueryService;
	@Resource
	private SignUpConditionService signUpConditionService;

	/**新增报名条件
	 * @Description
	 * @author wwb
	 * @Date 2021-07-09 17:39:49
	 * @param signUpCondition
	 * @return void
	*/
	public void add(SignUpCondition signUpCondition) {
		signUpConditionMapper.insert(signUpCondition);

		List<TemplateSignUpCondition> conditionDetails = handleDetailsInSignUpCondition(signUpCondition);
		if (CollectionUtils.isNotEmpty(conditionDetails)) {
			templateSignUpConditionMapper.batchAdd(conditionDetails);
		}
	}

	/**报名条件更新
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-04 11:19:22
	 * @param signUpCondition
	 * @return void
	 */
	public void update(SignUpCondition signUpCondition) {
		signUpConditionMapper.updateById(signUpCondition);
		// 删除所有明细
		templateSignUpConditionMapper.deleteByTemplateComponentId(signUpCondition.getTemplateComponentId());
		List<TemplateSignUpCondition> conditionDetails = handleDetailsInSignUpCondition(signUpCondition);
		if (CollectionUtils.isNotEmpty(conditionDetails)) {
			// 重新添加明细
			templateSignUpConditionMapper.batchAdd(signUpCondition.getTemplateConditionDetails());
		}
	}

	/**批量新增报名条件
	 * @Description
	 * @author wwb
	 * @Date 2021-07-09 17:42:10
	 * @param signUpConditions
	 * @return void
	*/
	public void batchAdd(List<SignUpCondition> signUpConditions) {
		if (CollectionUtils.isNotEmpty(signUpConditions)) {
			signUpConditionMapper.batchAdd(signUpConditions);
			List<TemplateSignUpCondition> conditionDetails = Lists.newArrayList();
			signUpConditions.forEach(v -> {
				conditionDetails.addAll(handleDetailsInSignUpCondition(v));
			});
			if (CollectionUtils.isNotEmpty(conditionDetails)) {
				templateSignUpConditionMapper.batchAdd(conditionDetails);
			}
		}
	}

	/**对signUpCondition 中的templateSignUpCondition列表进行数据处理
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-05 10:26:37
	 * @param signUpCondition
	 * @return java.util.List<com.chaoxing.activity.model.TemplateSignUpCondition>
	 */
	private List<TemplateSignUpCondition> handleDetailsInSignUpCondition(SignUpCondition signUpCondition) {
		List<TemplateSignUpCondition> conditionDetails = Optional.ofNullable(signUpCondition.getTemplateConditionDetails()).orElse(Lists.newArrayList());

		// 仅当基础条件为存在时，才进行明细的保存
		if (signUpCondition.getAllowSignedUp()) {
			// 设置模板组件id
			conditionDetails.forEach(v -> {
				v.setTemplateComponentId(signUpCondition.getTemplateComponentId());
			});
			// 如果活动创建时配置明细，则该报名条件不存明细的条件、明细的值
			if (signUpCondition.getConfigOnActivity()) {
				conditionDetails.forEach(v -> {
					v.setCondition(null);
					v.setValue(null);
				});
			}
			return conditionDetails;
		}
		return conditionDetails;
	}

	/**根据模版组件id删除
	 * @Description
	 * @author wwb
	 * @Date 2021-07-09 17:33:00
	 * @param templateComponentId
	 * @return void
	*/
	public void deleteByTemplateComponentId(Integer templateComponentId) {
		signUpConditionMapper.delete(new LambdaUpdateWrapper<SignUpCondition>()
			.eq(SignUpCondition::getTemplateComponentId, templateComponentId)
		);
	}

	/**根据模版组件id列表查询报名条件列表
	 * @Description
	 * @author wwb
	 * @Date 2021-07-14 17:24:23
	 * @param tplComponentIds
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	*/
	public List<SignUpCondition> listWithTemplateDetailsByTplComponentIds(List<Integer> tplComponentIds) {
		if (CollectionUtils.isEmpty(tplComponentIds)) {
			return Lists.newArrayList();
		}
		List<SignUpCondition> signUpConditions = signUpConditionMapper.selectList(new LambdaQueryWrapper<SignUpCondition>()
				.in(SignUpCondition::getTemplateComponentId, tplComponentIds));
		Map<Integer, List<TemplateSignUpCondition>> tplConditionDetailsMap = getTplConditionDetailsMap(tplComponentIds);
		signUpConditions.forEach(v -> {
			List<TemplateSignUpCondition> conditionDetails = Optional.ofNullable(tplConditionDetailsMap.get(v.getTemplateComponentId())).orElse(Lists.newArrayList());
			v.setTemplateConditionDetails(conditionDetails);
		});
		return signUpConditions;
	}

	/**根据报名条件模板组件id集合查询对应的模板报名条件明细列表map
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-03 17:02:37
	 * @param tplComponentIds
	 * @return java.util.Map<java.lang.Integer,java.util.List<com.chaoxing.activity.model.TemplateSignUpCondition>>
	 */
	private Map<Integer, List<TemplateSignUpCondition>> getTplConditionDetailsMap(List<Integer> tplComponentIds) {
		Map<Integer, List<TemplateSignUpCondition>> tplConditionsMap = Maps.newHashMap();
		if (CollectionUtils.isEmpty(tplComponentIds)) {
			return tplConditionsMap;
		}
		List<TemplateSignUpCondition> templateSignUpConditions = templateSignUpConditionMapper.selectList(new LambdaQueryWrapper<TemplateSignUpCondition>()
				.in(TemplateSignUpCondition::getTemplateComponentId, tplComponentIds));
		templateSignUpConditions.forEach(v -> {
			Integer tplComponentId = v.getTemplateComponentId();
			tplConditionsMap.computeIfAbsent(tplComponentId, k -> Lists.newArrayList());
			tplConditionsMap.get(tplComponentId).add(v);
		});
		return tplConditionsMap;
	}

	/**查询模板组件设置的报名条件列表
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-22 17:57:59
	 * @param templateComponentIds
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	*/
	private List<SignUpCondition> listByTemplateComponentIds(List<Integer> templateComponentIds, Integer activityId) {
		List<SignUpCondition> signUpConditions = signUpConditionMapper.selectList(new LambdaQueryWrapper<SignUpCondition>().in(SignUpCondition::getTemplateComponentId, templateComponentIds));
		// 封装报名条件明细
		if (CollectionUtils.isNotEmpty(signUpConditions)) {
			// 查询启用的报名条件
			List<Integer> enabledTemplateComponentIds = listActivityEnabledTemplateComponentId(activityId);
			signUpConditions = signUpConditions.stream().filter(v -> enabledTemplateComponentIds.contains(v.getTemplateComponentId())).collect(Collectors.toList());
			// 所有活动的条件明细
			List<ActivitySignUpCondition> allActivitySignUpConditions = activitySignUpConditionMapper.selectList(new LambdaQueryWrapper<ActivitySignUpCondition>()
					.eq(ActivitySignUpCondition::getActivityId, activityId));
			Map<Integer, List<ActivitySignUpCondition>> activityTemplateComponentIdValueMap = allActivitySignUpConditions.stream().collect(Collectors.groupingBy(ActivitySignUpCondition::getTemplateComponentId, Collectors.toList()));
			// 所有模板组件设置的条件明细
			List<ActivitySignUpCondition> allTemplateComponentActivitySignUpConditions = templateSignUpConditionMapper.selectList(new LambdaQueryWrapper<TemplateSignUpCondition>()
							.in(TemplateSignUpCondition::getTemplateComponentId, templateComponentIds))
					.stream().map(ActivitySignUpCondition::buildFromTemplateSignUpCondition).collect(Collectors.toList());
			Map<Integer, List<ActivitySignUpCondition>> templateComponentIdValueMap = allTemplateComponentActivitySignUpConditions.stream().collect(Collectors.groupingBy(ActivitySignUpCondition::getTemplateComponentId, Collectors.toList()));
			for (SignUpCondition signUpCondition : signUpConditions) {
				List<ActivitySignUpCondition> activitySignUpConditions = null;
				Integer sucTplComponentId = signUpCondition.getTemplateComponentId();
				if (sucTplComponentId == null || !signUpCondition.getAllowSignedUp()) {
					// 忽略
				}else {
					if (signUpCondition.getConfigOnActivity()) {
						activitySignUpConditions = activityTemplateComponentIdValueMap.get(sucTplComponentId);
					} else {
						activitySignUpConditions = templateComponentIdValueMap.get(sucTplComponentId);
					}
				}
				activitySignUpConditions = Optional.ofNullable(activitySignUpConditions).orElse(Lists.newArrayList());
				signUpCondition.setActivityConditionDetails(activitySignUpConditions);
			}
		}
		return signUpConditions;
	}

	/**是否能报名
	 * @Description
	 * 1、根据报名的templateComponentId找到报名条件的templateComponentId列表
	 * 2、根据signId找到活动id
	 * 3、根据活动id筛选出启用的报名条件templateComponentId列表
	 * 4、根据报名条件templateComponentId列表判断
	 * @author wwb
	 * @Date 2021-07-09 17:44:47
	 * @param uid
	 * @param signId
	 * @param templateComponentId 报名的模版组件id
	 * @return com.chaoxing.activity.dto.verification.UserSignUpAbleVerificationDTO
	*/
	public UserSignUpAbleVerificationDTO userCanSignUp(Integer uid, Integer signId, Integer templateComponentId) {
		UserSignUpAbleVerificationDTO userSignUpAbleVerification = userMatchSignUpCondition(uid, signId, templateComponentId);
		if (!userSignUpAbleVerification.getVerified()) {
			// 不满足报名条件
			return userSignUpAbleVerification;
		}
		Activity activity = activityQueryService.getBySignId(signId);
		Integer marketId = Optional.ofNullable(activity).map(Activity::getMarketId).orElse(null);
		if (marketId == null) {
			return userSignUpAbleVerification;
		}
		// 黑名单
		Blacklist userBlacklist = blacklistQueryService.getUserBlacklist(uid, marketId);
		if (userBlacklist != null) {
			UserBlacklistResultDTO userBlacklistResult = UserBlacklistResultDTO.buildFromBlacklist(userBlacklist, uid);
			userSignUpAbleVerification.inBlacklist(userBlacklistResult.getUnlockTime());
			return userSignUpAbleVerification;
		}
		// 验证活动市场的报名限制
		MarketSignUpConfig marketSignUpConfig = marketSignupConfigService.get(marketId);
		Integer signUpActivityLimit = Optional.ofNullable(marketSignUpConfig).map(MarketSignUpConfig::getSignUpActivityLimit).orElse(0);
		if (signUpActivityLimit < 1) {
			// 没做限制
			return userSignUpAbleVerification;
		}
		// 查询用户报名的活动（正在进行的）数量：先查询报名的signId列表，根据signId列表查询正在进行的活动数量
		List<Integer> signedUpSignIds = signApiService.listUserSignedUpSignIds(uid);
		if (CollectionUtils.isEmpty(signedUpSignIds)) {
			return userSignUpAbleVerification;
		}
		// 根据报名成功的报名签到id列表查询进行中的活动数量
		Integer signedUpActivityNum = activityQueryService.countIngActivityNumBySignIds(marketId, signedUpSignIds);
		if (signedUpActivityNum.compareTo(signUpActivityLimit) >= 0) {
			userSignUpAbleVerification.verifyFail("同时报名活动数超过限制");
			return userSignUpAbleVerification;
		}
		return userSignUpAbleVerification;
	}

	/**用户满足报名条件（忽略其他市场同时报名限制、黑名单等）
	 * @Description 
	 * @author wwb
	 * @Date 2021-11-10 13:00:06
	 * @param uid
	 * @param signId
	 * @param templateComponentId
	 * @return com.chaoxing.activity.dto.verification.UserSignUpAbleVerificationDTO
	*/
	private UserSignUpAbleVerificationDTO userMatchSignUpCondition(Integer uid, Integer signId, Integer templateComponentId) {
		UserSignUpAbleVerificationDTO userSignUpAbleVerification = UserSignUpAbleVerificationDTO.buildDefault();
		// 查询每一个报名条件
		List<TemplateComponent> signUpConditionTemplateComponents = templateComponentService.listSignUpConditionTemplateComponent(templateComponentId);
		if (CollectionUtils.isEmpty(signUpConditionTemplateComponents)) {
			return userSignUpAbleVerification;
		}
		List<Integer> templateComponentIds = signUpConditionTemplateComponents.stream().map(TemplateComponent::getId).collect(Collectors.toList());
		// 查询报名条件列表
		Activity activity = activityQueryService.getBySignId(signId);
		if (activity == null) {
			return userSignUpAbleVerification;
		}
		List<SignUpCondition> signUpConditions = listByTemplateComponentIds(templateComponentIds, activity.getId());
		if (CollectionUtils.isEmpty(signUpConditions)) {
			// 没有报名条件
			return userSignUpAbleVerification;
		}
		Map<Integer, SignUpCondition> templateComponentIdSignUpConditionMap = signUpConditions.stream().collect(Collectors.toMap(SignUpCondition::getTemplateComponentId, v -> v, (v1, v2) -> v2));
		for (TemplateComponent signUpConditionTemplateComponent : signUpConditionTemplateComponents) {
			// 找到匹配的报名条件
			SignUpCondition signUpCondition = templateComponentIdSignUpConditionMap.get(signUpConditionTemplateComponent.getId());
			if (signUpCondition == null) {
				continue;
			}
			if (!whetherCanSignUp(signUpCondition, uid)) {
				signUpConditionTemplateComponent.fillUserSignUpAbleVerification(userSignUpAbleVerification);
				return userSignUpAbleVerification;
			}
		}
		return userSignUpAbleVerification;
	}

	/**是否可以报名
	 * @Description
	 * @author wwb
	 * @Date 2021-07-16 10:55:36
	 * @param signUpCondition
	 * @param uid
	 * @return boolean
	*/
	private boolean whetherCanSignUp(SignUpCondition signUpCondition, Integer uid) {
		if (signUpCondition == null) {
			return true;
		}
		Integer fid = signUpCondition.getFid();
		Integer formId = Integer.parseInt(signUpCondition.getOriginIdentify());
		String fieldName = signUpCondition.getFieldName();
		// 查询表单结构
		List<FormStructureDTO> formStructure = wfwFormApiService.getFormStructure(formId, fid);
		// 获取字段别名
		String userFieldAlias = FormStructureDTO.getFieldAliasByLabel(formStructure, fieldName);
		// 获取表单记录列表
		List<FormDataDTO> wfwFormData = wfwFormApiService.listFormRecord(formId, fid);
		// 获取用户的表单记录列表
		List<FormDataDTO> userFormData = FormDataDTO.listUserFormData(wfwFormData, userFieldAlias, uid);
		// 用户在表单中的记录是否存在
		boolean formDataExist = CollectionUtils.isNotEmpty(userFormData);
		boolean formDataExistAllowSignUp = Optional.ofNullable(signUpCondition.getAllowSignedUp()).orElse(true);

		if (formDataExist != formDataExistAllowSignUp) {
			return false;
		}
		List<ActivitySignUpCondition> conditionDetails = signUpCondition.getActivityConditionDetails();
		if (CollectionUtils.isEmpty(conditionDetails)) {
			return true;
		}

		for (ActivitySignUpCondition detail : conditionDetails) {
			// 只要明细条件配置中有一项不符合条件，均不能报名
			if (!whetherCanSignUp(detail, formStructure, userFormData)) {
				return false;
			}
		}
		return true;
	}

	/**判断明细条件是否满足报名
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-05 13:52:24
	 * @param detail
	 * @param formStructure
	 * @param userFormData
	 * @return boolean
	 */
	private boolean whetherCanSignUp(ActivitySignUpCondition detail, List<FormStructureDTO> formStructure, List<FormDataDTO> userFormData) {
		String fieldAlias = FormStructureDTO.getFieldAliasByLabel(formStructure, detail.getFieldName());
		for (FormDataDTO formDatum : userFormData) {
			String formValue = WfwFormUtils.getValue(formDatum, fieldAlias);
			if (compareDetailValue(detail.getCondition(), detail.getValue(), formValue)) {
				return true;
			}
		}
		return false;
	}

	/**对比表单和明细配置的值
	* @Description
	* @author huxiaolong
	* @Date 2021-11-05 13:52:04
	* @param condition
	* @param value
	* @param formValue
	* @return boolean
	*/
	private boolean compareDetailValue(String condition, String value, String formValue) {
		ConditionEnum conditionEnum = ConditionEnum.fromValue(condition);
		if (conditionEnum == null) {
			return false;
		}
		formValue = Optional.ofNullable(formValue).orElse("");
		value = Optional.ofNullable(value).orElse("");
		switch (conditionEnum) {
			case NO_LIMIT:
				return true;
			case EQUALS:
				return Objects.equals(formValue, value);
			case UN_EQUALS:
				return !Objects.equals(formValue, value);
			case INCLUDE:
				return formValue.contains(value);
			case EXCLUDE:
				return !formValue.contains(value);
			case EMPTY:
				return StringUtils.isBlank(formValue);
			case UN_EMPTY:
				return StringUtils.isNotBlank(formValue);
			case GREATER_THAN:
				return formValue.compareTo(value) > 0;
			case LESS_THAN:
				return formValue.compareTo(value) < 0;
			case GREATER_THAN_OR_EQUALS:
				return formValue.compareTo(value) >= 0;
			case LESS_THEN_OR_EQUALS:
				return formValue.compareTo(value) <= 0;
			default:
				return false;
		}
	}

	/**查询活动启用的报名条件模版组件id
	 * @Description
	 * @author wwb
	 * @Date 2021-07-21 18:57:53
	 * @param activityId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listActivityEnabledTemplateComponentId(Integer activityId) {
		List<SignUpConditionEnable> signUpConditionEnables = signUpConditionEnableMapper.selectList(new LambdaQueryWrapper<SignUpConditionEnable>()
				.eq(SignUpConditionEnable::getActivityId, activityId)
		);
		return Optional.ofNullable(signUpConditionEnables).orElse(Lists.newArrayList()).stream().map(SignUpConditionEnable::getTemplateComponentId).collect(Collectors.toList());
	}

	/**保存活动关联报名条件的启用列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-04 17:05:40
	 * @param activityId
	 * @param tplComponentIds
	 * @return void
	 */
	public void saveActivitySignUpConditionEnables(Integer activityId, List<Integer> tplComponentIds) {
		if (CollectionUtils.isEmpty(tplComponentIds)) {
			return;
		}
		List<SignUpConditionEnable> waitSaveEnables = tplComponentIds.stream().map(
						v -> SignUpConditionEnable.builder()
								.activityId(activityId)
								.templateComponentId(v)
								.build())
				.collect(Collectors.toList());
		signUpConditionEnableMapper.batchAdd(waitSaveEnables);
	}

	/**更新活动报名的报名条件启用
	* @Description
	* @author huxiaolong
	* @Date 2021-07-21 19:19:15
	* @param activityId
	* @param signUpTplComponentIds
	* @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void updateActivitySignUpEnables(Integer activityId, List<Integer> signUpTplComponentIds) {
		// 已存在启用的报名条件模板组件id列表
		List<Integer> existEnableTplComponentIds = signUpConditionEnableMapper.selectList(new LambdaQueryWrapper<SignUpConditionEnable>()
				.select(SignUpConditionEnable::getTemplateComponentId).eq(SignUpConditionEnable::getActivityId, activityId))
				.stream().map(SignUpConditionEnable::getTemplateComponentId).collect(Collectors.toList());
		// 先删除活动启用关联表
		if (CollectionUtils.isNotEmpty(existEnableTplComponentIds)) {
			signUpConditionEnableMapper.delete(new LambdaQueryWrapper<SignUpConditionEnable>()
					.eq(SignUpConditionEnable::getActivityId, activityId)
					.in(SignUpConditionEnable::getTemplateComponentId, existEnableTplComponentIds));
		}
		// 新增活动启用关联
		ApplicationContextHolder.getBean(SignUpConditionService.class).saveActivitySignUpConditionEnables(activityId, signUpTplComponentIds);
		// 找出已停用的报名条件模板组件id
		if (CollectionUtils.isNotEmpty(signUpTplComponentIds)) {
			existEnableTplComponentIds.removeAll(signUpTplComponentIds);
		}
		if (CollectionUtils.isNotEmpty(existEnableTplComponentIds)) {
			// 删除已经停用的报名条件明细配置
			activitySignUpConditionMapper.delete(new LambdaQueryWrapper<ActivitySignUpCondition>()
					.eq(ActivitySignUpCondition::getActivityId, activityId)
					.in(ActivitySignUpCondition::getTemplateComponentId, existEnableTplComponentIds));
		}
	}

	/**新增活动报名的报名条件明细配置
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-04 17:21:42
	 * @param activityId
	 * @param signUpConditions
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void saveActivitySignUpConditionsFromConditions(Integer activityId, List<SignUpCondition> signUpConditions) {
		// 获取活动报名条件明细，保存活动报名条件明细配置
		if (CollectionUtils.isEmpty(signUpConditions)) {
			return;
		}
		List<ActivitySignUpCondition> activitySignUpConditions = Lists.newArrayList();
		signUpConditions.forEach(v -> {
			v.getActivityConditionDetails().forEach(u -> {
				u.setActivityId(activityId);
				u.setTemplateComponentId(v.getTemplateComponentId());
			});
			activitySignUpConditions.addAll(v.getActivityConditionDetails());
		});
		if (CollectionUtils.isEmpty(activitySignUpConditions)) {
			return;
		}
		activitySignUpConditionMapper.batchAdd(activitySignUpConditions);
	}

	/**更新活动报名的报名条件明细配置
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-07-21 19:14:37
	 * @param activityId
	 * @param signUpConditions
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void updateActivitySignUpConditionsFromConditions(Integer activityId, List<SignUpCondition> signUpConditions) {
		// 获取活动报名条件明细，更新活动报名条件明细配置
		if (CollectionUtils.isEmpty(signUpConditions)) {
			return;
		}
		List<ActivitySignUpCondition> waitSaveData = Lists.newArrayList();
		// 活动创建后，可能报名条件均为启用，但模板明细配置存在，在编辑的时候启用报名条件，此时活动明细配置需要进行新增
		signUpConditions.forEach(v -> {
			if (CollectionUtils.isNotEmpty(v.getDeleteDetailIds())) {
				activitySignUpConditionMapper.deleteBatchIds(v.getDeleteDetailIds());
			}
			List<ActivitySignUpCondition> activityConditionDetails = v.getActivityConditionDetails();
			if (CollectionUtils.isNotEmpty(activityConditionDetails)) {
				v.getActivityConditionDetails().forEach(u -> {
					if (u.getId() == null) {
						u.setActivityId(activityId);
						u.setTemplateComponentId(v.getTemplateComponentId());
						waitSaveData.add(u);
					} else {
						activitySignUpConditionMapper.updateById(u);
					}
				});
			}
		});
		if (CollectionUtils.isEmpty(waitSaveData)) {
			return;
		}
		activitySignUpConditionMapper.batchAdd(waitSaveData);
	}

	/**查询模板下所有报名条件，且报名条件中的明细均转换为activity_sign_up_condition
	* @Description
	* @author huxiaolong
	* @Date 2021-11-03 18:13:13
	* @param templateId
	* @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	*/
	public List<SignUpCondition> listWithActivityConditionsByTemplate(Integer templateId) {
		if (templateId == null) {
			return Lists.newArrayList();
		}
		List<Integer> tplComponentIds = templateComponentService.listSignUpConditionTplComponents(templateId)
				.stream().map(TemplateComponent::getId).collect(Collectors.toList());
		return listWithActivityConditionsByTplComponentIds(tplComponentIds);
	}

	/**查询templateComponentIds对应的报名条件列表，且报名条件中的明细均转换为activity_sign_up_condition
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-03 16:53:22
	 * @param tplComponentIds
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	 */
	public List<SignUpCondition> listWithActivityConditionsByTplComponentIds(List<Integer> tplComponentIds) {
		if (CollectionUtils.isEmpty(tplComponentIds)) {
			return Lists.newArrayList();
		}
		List<SignUpCondition> signUpConditions = signUpConditionMapper.selectList(new LambdaQueryWrapper<SignUpCondition>()
				.in(SignUpCondition::getTemplateComponentId, tplComponentIds));
		List<Integer> configOnActivityIds = signUpConditions.stream()
				.filter(v -> v.getAllowSignedUp() && v.getConfigOnActivity())
				.map(SignUpCondition::getTemplateComponentId).collect(Collectors.toList());
		Map<Integer, List<TemplateSignUpCondition>> tplConditionDetailsMap = getTplConditionDetailsMap(configOnActivityIds);
		if (MapUtils.isEmpty(tplConditionDetailsMap)) {
			return signUpConditions;
		}
		signUpConditions.forEach(v -> {
			List<TemplateSignUpCondition> tmpConditionDetails = tplConditionDetailsMap.get(v.getTemplateComponentId());
			if (tmpConditionDetails != null) {
				v.setTemplateConditionDetails(tmpConditionDetails);
				v.setActivityConditionDetails(ActivitySignUpCondition.buildFromTemplateSignUpConditions(tmpConditionDetails));
			}
		});
		return signUpConditions;
	}

	/**在编辑活动时，查询活动启用的报名条件及其明细
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-03 17:02:14
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	 */
	public List<SignUpCondition> listEditActivityConditions(Integer activityId, Integer templateId) {
		// 查询模板所有的报名条件，转换为活动报名条件明细
		List<SignUpCondition> result = listWithActivityConditionsByTemplate(templateId);
		if (CollectionUtils.isEmpty(result)) {
			return result;
		}
		// 查询活动的报名条件明细
		List<ActivitySignUpCondition> activitySignUpConditions = activitySignUpConditionMapper.selectList(new LambdaQueryWrapper<ActivitySignUpCondition>()
				.eq(ActivitySignUpCondition::getActivityId, activityId));
		Map<Integer, List<ActivitySignUpCondition>> tplComponentIdDetailsMap = Maps.newHashMap();
		activitySignUpConditions.forEach(v -> {
			Integer tplComponentId = v.getTemplateComponentId();
			tplComponentIdDetailsMap.computeIfAbsent(tplComponentId, k -> Lists.newArrayList());
			tplComponentIdDetailsMap.get(tplComponentId).add(v);
		});
		// 活动的报名条件明细覆写到模板的默认报名条件明细
		result.forEach(v -> {
			if (v.getAllowSignedUp() && v.getConfigOnActivity() && CollectionUtils.isNotEmpty(v.getActivityConditionDetails())) {
				Integer tplComponentId = v.getTemplateComponentId();
				List<ActivitySignUpCondition> signUpConditionDetails = Optional.ofNullable(tplComponentIdDetailsMap.get(tplComponentId)).orElse(Lists.newArrayList());
				List<String> fieldNames = v.getActivityConditionDetails().stream().map(ActivitySignUpCondition::getFieldName).collect(Collectors.toList());
				List<ActivitySignUpCondition> details = signUpConditionDetails.stream().filter(u -> fieldNames.contains(u.getFieldName())).collect(Collectors.toList());
				v.setActivityConditionDetails(details);
			}
		});
		return result;
	}

}