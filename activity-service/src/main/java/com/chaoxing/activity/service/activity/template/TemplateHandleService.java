package com.chaoxing.activity.service.activity.template;

import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.mapper.TemplateComponentMapper;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.engine.SignUpFillInfoTypeService;
import com.chaoxing.activity.service.activity.market.ActivityMarketQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**模版处理服务
 * @author wwb
 * @version ver 1.0
 * @className TemplateHandleService
 * @description
 * @blame wwb
 * @date 2021-07-14 16:50:50
 */
@Slf4j
@Service
public class TemplateHandleService {

	@Resource
	private TemplateMapper templateMapper;
	@Resource
	private TemplateComponentMapper templateComponentMapper;

	@Resource
	private ActivityMarketQueryService activityMarketQueryService;
	@Resource
	private TemplateQueryService templateQueryService;
	@Resource
	private SignUpConditionService signUpConditionService;
	@Resource
	private SignUpFillInfoTypeService signUpFillInfoTypeService;

	/**新增模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 17:44:00
	 * @param template
	 * @param operateUserDto
	 * @return void
	*/
	public void add(Template template, OperateUserDTO operateUserDto) {
		template.perfectCreator(operateUserDto);
		templateMapper.insert(template);
	}

	/**批量新增模版组件
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 18:12:06
	 * @param templateComponents
	 * @return void
	*/
	public void batchAddTemplateComponents(List<TemplateComponent> templateComponents) {
		if (CollectionUtils.isNotEmpty(templateComponents)) {
			templateComponentMapper.batchAdd(templateComponents);
			templateComponents.stream().forEach(templateComponent -> {
				Optional.ofNullable(templateComponent.getChildren()).orElse(Lists.newArrayList()).forEach(v -> v.setPid(templateComponent.getId()));
				batchAddTemplateComponents(templateComponent.getChildren());
			});
		}
	}

	/**克隆模版
	 * @Description
	 * 1、查询模版
	 * 2、查询模版组件关联并克隆
	 * 3、查询报名条件并克隆
	 * 4、查询报名填写信息类型并克隆
	 * @author wwb
	 * @Date 2021-07-14 16:52:20
	 * @param marketId
	 * @param originTemplateId
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void cloneTemplate(Integer marketId, Integer originTemplateId) {
		ActivityMarket activityMarket = activityMarketQueryService.getById(marketId);
		Template originTemplate = templateQueryService.getById(originTemplateId);
		Optional.ofNullable(originTemplate).orElseThrow(() -> new BusinessException("模版不存在"));
		List<TemplateComponent> originTemplateComponents = templateQueryService.listTemplateComponentByTemplateId(originTemplateId);
		List<Integer> templateComponentIds = Optional.ofNullable(originTemplateComponents).orElse(Lists.newArrayList()).stream().map(TemplateComponent::getId).collect(Collectors.toList());
		List<SignUpCondition> originSignUpConditions = signUpConditionService.listByTemplateComponentIds(templateComponentIds);
		List<SignUpFillInfoType> originSignUpFillInfoTypes = signUpFillInfoTypeService.listByTemplateComponentIds(templateComponentIds);
		// 克隆
		OperateUserDTO operateUserDto = OperateUserDTO.build(activityMarket.getCreateUid());
		// 克隆模版
		Template template = originTemplate.cloneToNewMarket(marketId, activityMarket.getFid());
		add(template, operateUserDto);
		Integer templateId = template.getId();
		// 克隆模版组件列表
		List<TemplateComponent> templateComponents = TemplateComponent.cloneToNewTemplateId(originTemplateComponents, templateId);
		batchAddTemplateComponents(templateComponents);
		// 找到新旧templateComponentId的对应关系
		Map<Integer, Integer> oldNewTemplateComponentIdRelation = Maps.newHashMap();
		for (int i = 0; i < originTemplateComponents.size(); i++) {
			oldNewTemplateComponentIdRelation.put(originTemplateComponents.get(i).getId(), templateComponents.get(i).getId());
		}
		// 克隆报名条件列表
		List<SignUpCondition> signUpConditions = SignUpCondition.cloneToNewTemplateComponentId(originSignUpConditions, oldNewTemplateComponentIdRelation);
		signUpConditionService.batchAdd(signUpConditions);
		// 克隆报名填报信息类型列表
		List<SignUpFillInfoType> signUpFillInfoTypes = SignUpFillInfoType.cloneToNewTemplateComponentId(originSignUpFillInfoTypes, oldNewTemplateComponentIdRelation);

	}

}