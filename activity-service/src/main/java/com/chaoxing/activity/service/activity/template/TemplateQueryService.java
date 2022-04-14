package com.chaoxing.activity.service.activity.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.dto.query.QueryFilterDTO;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ComponentField;
import com.chaoxing.activity.model.Template;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**模版查询服务服务
 * @author wwb
 * @version ver 1.0
 * @className TemplateQueryService
 * @description
 * @blame wwb
 * @date 2021-07-13 19:26:44
 */
@Slf4j
@Service
public class TemplateQueryService {

	@Resource
	private TemplateMapper templateMapper;
	@Resource
	private ComponentFieldMapper componentFieldMapper;

	/**根据id查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 19:28:59
	 * @param id
	 * @return com.chaoxing.activity.model.Template
	*/
	public Template getById(Integer id) {
		return templateMapper.selectById(id);
	}

	/**根据活动标识查询系统模版id
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 16:53:49
	 * @param activityFlagEnum
	 * @return java.lang.Integer
	*/
	public Integer getSystemTemplateIdByActivityFlag(Activity.ActivityFlagEnum activityFlagEnum) {
		Template template = getSystemTemplateByActivityFlag(activityFlagEnum);
		return template == null ? null : template.getId();
	}

	/**根据活动标识查询系统模版
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-25 14:45:22
	* @param activityFlagEnum
	* @return com.chaoxing.activity.model.Template
	*/
	public Template getSystemTemplateByActivityFlag(Activity.ActivityFlagEnum activityFlagEnum) {
		List<Template> systemTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
				.eq(Template::getSystem, true)
				.eq(Template::getActivityFlag, activityFlagEnum.getValue())
		);
		return Optional.ofNullable(systemTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**根据活动标识找到机构下对应的模版（存在多个则返回最后一个）
	 * @Description
	 * 1、根据活动标识找到系统模版
	 * 2、根据系统模版找到机构的模版
	 * @author wwb
	 * @Date 2021-07-14 20:45:21
	 * @param fid
	 * @param activityFlagEnum
	 * @return com.chaoxing.activity.model.Template
	*/
	public Template getOrgTemplateByActivityFlag(Integer fid, Activity.ActivityFlagEnum activityFlagEnum) {
		List<Template> orgTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
				.eq(Template::getActivityFlag, activityFlagEnum.getValue())
				.eq(Template::getFid, fid)
		);
		if (CollectionUtils.isEmpty(orgTemplates)) {
			return null;
		}
		return orgTemplates.get(orgTemplates.size() - 1);
	}

	/**根据活动市场id获取模版，市场id为空则根据activityFlag查询系统模版
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 11:51:29
	 * @param marketId
	 * @param activityFlagEnum
	 * @return com.chaoxing.activity.model.Template
	*/
	public Template getTemplateByMarketIdOrActivityFlag(Integer marketId, Activity.ActivityFlagEnum activityFlagEnum) {
		Template template = null;
		if (marketId != null) {
			template = getMarketFirstTemplate(marketId);
		}
		if (template == null) {
			List<Template> systemTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
					.eq(Template::getSystem, true)
					.eq(Template::getActivityFlag, activityFlagEnum.getValue()));
			template = Optional.ofNullable(systemTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
		}
		return template;
	}

	/**获取活动市场第一个模板
	* @Description 
	* @author huxiaolong
	* @Date 2021-07-26 17:31:49
	* @param marketId
	* @return com.chaoxing.activity.model.Template
	*/
	public Template getMarketFirstTemplate(Integer marketId) {
		List<Template> marketTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
				.eq(Template::getMarketId, marketId));
		return Optional.ofNullable(marketTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**获取模版自定义组件的查询过滤对象列表
	 * @Description
	 * 查询组件类型为选择的
	 * @author wwb
	 * @Date 2021-12-27 17:07:36
	 * @param templateId
	 * @return java.util.Map<java.lang.Integer,java.util.List<com.chaoxing.activity.dto.query.QueryFilterDTO>>
	*/
	public Map<Integer, List<QueryFilterDTO>> getTemplateCustomQueryFilterMapping(Integer templateId) {
		List<ComponentField> componentFields = componentFieldMapper.listByTemplateId(templateId);
		return componentFields.stream().collect(Collectors.groupingBy(ComponentField::getComponentId, Collectors.mapping(v -> QueryFilterDTO.builder().text(v.getFieldName()).value(v.getFieldName()).build(), Collectors.toList())));
	}

	/**根据活动市场id获取模版自定义组件的查询过滤对象列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-12-27 17:26:49
	 * @param marketId
	 * @return java.util.Map<java.lang.Integer,java.util.List<com.chaoxing.activity.dto.query.QueryFilterDTO>>
	*/
	public Map<Integer, List<QueryFilterDTO>> getTemplateCustomQueryFilterMappingByMarketId(Integer marketId) {
		Template marketFirstTemplate = getMarketFirstTemplate(marketId);
		if (marketFirstTemplate == null) {
			return Maps.newHashMap();
		}
		List<ComponentField> componentFields = componentFieldMapper.listByTemplateId(marketFirstTemplate.getId());
		return componentFields.stream().collect(Collectors.groupingBy(ComponentField::getComponentId, Collectors.mapping(v -> QueryFilterDTO.builder().text(v.getFieldName()).value(v.getFieldName()).build(), Collectors.toList())));
	}

}