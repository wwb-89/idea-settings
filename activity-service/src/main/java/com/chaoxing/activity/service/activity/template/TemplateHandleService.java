package com.chaoxing.activity.service.activity.template;

import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.engine.SignUpConditionService;
import com.chaoxing.activity.service.activity.engine.SignUpFillInfoTypeService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
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
	private TemplateComponentService templateComponentService;
	@Resource
	private MarketQueryService activityMarketQueryService;
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
		Market activityMarket = activityMarketQueryService.getById(marketId);
		Template originTemplate = templateQueryService.getById(originTemplateId);
		Optional.ofNullable(originTemplate).orElseThrow(() -> new BusinessException("模版不存在"));

		List<TemplateComponent> originTemplateComponents = templateComponentService.listTemplateComponentByTemplateId(originTemplateId);
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
		List<TemplateComponent> parentTemplateComponents = TemplateComponent.cloneToNewTemplateId(originTemplateComponents, templateId);
		templateComponentService.batchAddTemplateComponents(parentTemplateComponents);
		// 找到新旧templateComponentId的对应关系
		List<TemplateComponent> templateComponents = Lists.newArrayList(parentTemplateComponents);
		parentTemplateComponents.forEach(v -> templateComponents.addAll(v.getChildren()));
		Map<Integer, Integer> oldNewTemplateComponentIdRelation = templateComponents.stream().collect(Collectors.toMap(TemplateComponent::getOriginId, TemplateComponent::getId));
		// 克隆报名条件列表
		List<SignUpCondition> signUpConditions = SignUpCondition.cloneToNewTemplateComponentId(originSignUpConditions, oldNewTemplateComponentIdRelation);
		signUpConditionService.batchAdd(signUpConditions);
		// 克隆报名填报信息类型列表
		List<SignUpFillInfoType> signUpFillInfoTypes = SignUpFillInfoType.cloneToNewTemplateComponentId(originSignUpFillInfoTypes, oldNewTemplateComponentIdRelation);
		signUpFillInfoTypeService.batchAdd(signUpFillInfoTypes);
	}

	/**更新模板
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-25 15:35:43
	* @param template
	* @return void
	*/
	@Transactional(rollbackFor = Exception.class)
    public void update(Template template) {
		if (template == null || template.getId() == null) {
			return;
		}
		templateMapper.updateById(template);
    }
}