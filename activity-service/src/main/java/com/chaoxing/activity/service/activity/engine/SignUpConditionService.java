package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.activity.create.ActivityCreateParamDTO;
import com.chaoxing.activity.mapper.ActivitySignUpConditionMapper;
import com.chaoxing.activity.mapper.SignUpConditionEnableMapper;
import com.chaoxing.activity.mapper.SignUpConditionMapper;
import com.chaoxing.activity.mapper.TemplateSignUpConditionMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.util.ApplicationContextHolder;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
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
	private MarketQueryService marketQueryService;
	@Resource
	private SignApiService signApiService;

	/**新增报名条件
	 * @Description
	 * @author wwb
	 * @Date 2021-07-09 17:39:49
	 * @param signUpCondition
	 * @return void
	*/
	public void add(SignUpCondition signUpCondition) {
		signUpConditionMapper.insert(signUpCondition);

		List<TemplateSignUpCondition> conditionDetails = getDetailsFromSignUpCondition(signUpCondition);
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
		// 查询已存在的模板报名条件明细列表
		Integer tplComponentId = signUpCondition.getTemplateComponentId();
		List<TemplateSignUpCondition> waitHandleConditionDetails = Optional.ofNullable(signUpCondition.getTemplateConditionDetails()).orElse(Lists.newArrayList());
		List<TemplateSignUpCondition> existConditionDetails = templateSignUpConditionMapper.selectList(new LambdaQueryWrapper<TemplateSignUpCondition>()
				.eq(TemplateSignUpCondition::getTemplateComponentId, tplComponentId));
		// 已存在报名条件明细id
		List<Integer> existConditionDetailIds = existConditionDetails.stream().map(TemplateSignUpCondition::getId).collect(Collectors.toList());
		// 若编辑后的报名条件无明细，而现存报名条件对应的明细，则删除现存的明细
		if (CollectionUtils.isEmpty(waitHandleConditionDetails) && CollectionUtils.isNotEmpty(existConditionDetailIds)) {
			templateSignUpConditionMapper.deleteBatchIds(existConditionDetailIds);
			return;
		}
		Map<String, TemplateSignUpCondition> tplSignUpConditionNameMap = waitHandleConditionDetails.stream()
				.collect(Collectors.toMap(TemplateSignUpCondition::getFieldName, v -> v, (v1, v2) -> v2));
		Map<String, TemplateSignUpCondition> existTplSignUpConditionNameMap = existConditionDetails.stream()
				.collect(Collectors.toMap(TemplateSignUpCondition::getFieldName, v -> v, (v1, v2) -> v2));

		List<TemplateSignUpCondition> waitSaveData = Lists.newArrayList();
		List<Integer> waitUpdateIds = Lists.newArrayList();
		for (Map.Entry<String, TemplateSignUpCondition> entry : tplSignUpConditionNameMap.entrySet()) {
			String name = entry.getKey();
			TemplateSignUpCondition item = entry.getValue();
			TemplateSignUpCondition existItem = existTplSignUpConditionNameMap.get(name);

			item.setTemplateComponentId(tplComponentId);
			if (existItem != null) {
				Integer existDetailId = existItem.getId();
				item.setId(existDetailId);
				waitUpdateIds.add(existDetailId);
				// 更新
				templateSignUpConditionMapper.updateById(item);

			} else {
				waitSaveData.add(item);
			}
		}
		if (CollectionUtils.isNotEmpty(waitSaveData)) {
			templateSignUpConditionMapper.batchAdd(waitSaveData);
		}
		List<Integer> waitDeleteIds = existConditionDetailIds.stream().filter(v -> !waitUpdateIds.contains(v)).collect(Collectors.toList());
		if (CollectionUtils.isNotEmpty(waitDeleteIds)) {
			templateSignUpConditionMapper.deleteBatchIds(waitDeleteIds);
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
				conditionDetails.addAll(getDetailsFromSignUpCondition(v));
			});
			if (CollectionUtils.isNotEmpty(conditionDetails)) {
				templateSignUpConditionMapper.batchAdd(conditionDetails);
			}
		}
	}

	private List<TemplateSignUpCondition> getDetailsFromSignUpCondition(SignUpCondition signUpCondition) {
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

	/**根据模版组件id查询报名条件
	 * @Description
	 * @author wwb
	 * @Date 2021-07-16 10:32:57
	 * @param signId
	 * @param templateComponentId
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	*/
	public List<SignUpCondition> listBySignUp(Integer signId, Integer templateComponentId) {
		List<TemplateComponent> subTemplateComponents = templateComponentService.listSubTemplateComponent(templateComponentId);
		List<Integer> subTemplateComponentIds = Optional.ofNullable(subTemplateComponents).orElse(Lists.newArrayList()).stream().map(TemplateComponent::getId).collect(Collectors.toList());
		List<SignUpCondition> signUpConditions;
		if (CollectionUtils.isNotEmpty(subTemplateComponentIds)) {
			signUpConditions = signUpConditionMapper.selectList(new LambdaQueryWrapper<SignUpCondition>()
					.in(SignUpCondition::getTemplateComponentId, subTemplateComponentIds)
			);
		} else {
			signUpConditions = Lists.newArrayList();
		}
		if (CollectionUtils.isNotEmpty(signUpConditions)) {
			Activity activity = activityQueryService.getBySignId(signId);
			// 查询启用的报名条件
			List<Integer> enabledTemplateComponentIds = listActivityEnabledTemplateComponentId(activity.getId());
			signUpConditions = signUpConditions.stream().filter(v -> enabledTemplateComponentIds.contains(v.getTemplateComponentId())).collect(Collectors.toList());
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
	 * @return boolean
	*/
	public void userCanSignUp(Integer uid, Integer signId, Integer templateComponentId) {
		List<SignUpCondition> signUpConditions = listBySignUp(signId, templateComponentId);
		boolean signUpConditionValidate = false;
		if (CollectionUtils.isNotEmpty(signUpConditions)) {
			for (SignUpCondition signUpCondition : signUpConditions) {
				if (whetherCanSignUp(signUpCondition, uid)) {
					signUpConditionValidate = true;
					break;
				}
			}
		}else {
			signUpConditionValidate = true;
		}
		if (!signUpConditionValidate) {
			throw new BusinessException("不满足报名条件");
		}
		// 验证活动市场的报名限制
		Activity activity = activityQueryService.getBySignId(signId);
		Integer marketId = Optional.ofNullable(activity).map(Activity::getMarketId).orElse(null);
		if (marketId == null) {
			return;
		}
		Market market = marketQueryService.getById(marketId);
		Integer signUpActivityLimit = Optional.ofNullable(market).map(Market::getSignUpActivityLimit).orElse(0);
		if (signUpActivityLimit < 1) {
			// 没做限制
			return;
		}
		// 查询用户报名的活动（正在进行的）数量：先查询报名的signId列表，根据signId列表查询正在进行的活动数量
		List<Integer> signedUpSignIds = signApiService.listUserSignedUpSignIds(uid);
		if (CollectionUtils.isEmpty(signedUpSignIds)) {
			return;
		}
		// 根据报名成功的报名签到id列表查询进行中的活动数量
		Integer signedUpActivityNum = activityQueryService.countIngActivityNumBySignIds(marketId, signedUpSignIds);
		if (signedUpActivityNum.compareTo(signUpActivityLimit) >= 0) {
			throw new BusinessException("同时报名活动数超过限制");
		}
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
		String originIdentify = signUpCondition.getOriginIdentify();
		String fieldName = signUpCondition.getFieldName();
		// 从表单中的字段中拉取uid列表
		List<Integer> uids = wfwFormApiService.listFormFieldUid(fid, Integer.parseInt(originIdentify), fieldName);
		boolean exist = uids.contains(uid);
		return exist == Optional.ofNullable(signUpCondition.getAllowSignedUp()).orElse(true);
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
		existEnableTplComponentIds.removeAll(signUpTplComponentIds);
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
		List<Integer> waitDeleteTplComponentIds = Lists.newArrayList();
		// 活动创建后，可能报名条件均为启用，但模板明细配置存在，在编辑的时候启用报名条件，此时活动明细配置需要进行新增
		signUpConditions.forEach(v -> v.getActivityConditionDetails().forEach(u -> {
			if (u.getId() == null) {
				u.setActivityId(activityId);
				u.setTemplateComponentId(v.getTemplateComponentId());
				waitSaveData.add(u);
			} else {
				activitySignUpConditionMapper.updateById(u);
			}
		}));
		if (CollectionUtils.isEmpty(waitSaveData)) {
			return;
		}
		activitySignUpConditionMapper.batchAdd(waitSaveData);
	}


	/**查询出启用的报名条件模板组件id列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-11-04 14:44:40
	 * @param activityId
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listEnableSignUpConditionTplComponentIds(Integer activityId) {
		return signUpConditionEnableMapper.selectList(new QueryWrapper<SignUpConditionEnable>()
				.lambda()
				.eq(SignUpConditionEnable::getActivityId, activityId))
				.stream().map(SignUpConditionEnable::getTemplateComponentId)
				.collect(Collectors.toList());
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
		List<SignUpCondition> result = listWithActivityConditionsByTemplate(templateId);
		if (CollectionUtils.isEmpty(result)) {
			return result;
		}
		List<ActivitySignUpCondition> activitySignUpConditions = activitySignUpConditionMapper.selectList(new LambdaQueryWrapper<ActivitySignUpCondition>()
				.eq(ActivitySignUpCondition::getActivityId, activityId));
		Map<Integer, Map<String, ActivitySignUpCondition>> tplComponentIdNameDetailMap = Maps.newHashMap();
		activitySignUpConditions.forEach(v -> {
			Integer tplComponentId = v.getTemplateComponentId();
			tplComponentIdNameDetailMap.computeIfAbsent(tplComponentId, k -> Maps.newHashMap());
			tplComponentIdNameDetailMap.get(tplComponentId).put(v.getFieldName(), v);
		});
		result.forEach(v -> {
			if (v.getAllowSignedUp() && v.getConfigOnActivity() && CollectionUtils.isNotEmpty(v.getActivityConditionDetails())) {
				Map<String, ActivitySignUpCondition> itemMap = Optional.ofNullable(tplComponentIdNameDetailMap.get(v.getTemplateComponentId())).orElse(Maps.newHashMap());
				v.getActivityConditionDetails().forEach(u -> {
					ActivitySignUpCondition detail = itemMap.get(u.getFieldName());
					if (detail != null) {
						BeanUtils.copyProperties(detail, u);
					}
				});
			}
		});
		return result;
	}

}