package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.SignUpConditionEnableMapper;
import com.chaoxing.activity.mapper.SignUpConditionMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.SignUpCondition;
import com.chaoxing.activity.model.SignUpConditionEnable;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.chaoxing.activity.service.manager.WfwFormApiService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
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
	private WfwFormApiService wfwFormApiService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private ActivityQueryService activityQueryService;

	/**新增报名条件
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-09 17:39:49
	 * @param signUpCondition
	 * @return void
	*/
	public void add(SignUpCondition signUpCondition) {
		signUpConditionMapper.insert(signUpCondition);
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
		}
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
	 * @param templateComponentIds
	 * @return java.util.List<com.chaoxing.activity.model.SignUpCondition>
	*/
	public List<SignUpCondition> listByTemplateComponentIds(List<Integer> templateComponentIds) {
		if (CollectionUtils.isNotEmpty(templateComponentIds)) {
			return signUpConditionMapper.selectList(new LambdaQueryWrapper<SignUpCondition>()
					.in(SignUpCondition::getTemplateComponentId, templateComponentIds)
			);
		}
		return Lists.newArrayList();
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
		List<TemplateComponent> subTemplateComponents = templateQueryService.listSubTemplateComponent(templateComponentId);
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
	public boolean whetherCanSignUp(Integer uid, Integer signId, Integer templateComponentId) {
		List<SignUpCondition> signUpConditions = listBySignUp(signId, templateComponentId);
		if (CollectionUtils.isEmpty(signUpConditions)) {
			return true;
		}
		for (SignUpCondition signUpCondition : signUpConditions) {
			if (whetherCanSignUp(signUpCondition, uid)) {
				return true;
			}
		}
		return false;
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
		return !(exist ^ Optional.ofNullable(signUpCondition.getAllowSignedUp()).orElse(true));
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
}