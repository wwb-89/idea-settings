package com.chaoxing.activity.service.activity.template;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.TemplateComponentMapper;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.model.TemplateComponent;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
	private TemplateComponentMapper templateComponentMapper;

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

	/**根据模版id查询源模版的活动标识
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-13 23:14:38
	 * @param templateId
	 * @return java.lang.String
	*/
	public String getActivityFlagByTemplateId(Integer templateId) {
		Template template = getById(templateId);
		Integer originTemplateId = template.getOriginTemplateId();
		Template originTemplate = getById(originTemplateId);
		return Optional.ofNullable(originTemplate).map(Template::getActivityFlag).filter(StringUtils::isNotBlank).orElse(Activity.ActivityFlagEnum.NORMAL.getValue());
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

	/**根据模版id查询模版组件关联
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-14 17:19:04
	 * @param templateId
	 * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
	*/
	public List<TemplateComponent> listTemplateComponentByTemplateId(Integer templateId) {
		return templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
				.eq(TemplateComponent::getTemplateId, templateId)
				.eq(TemplateComponent::getDeleted, false)
		);
	}

	/**根据活动标识找到机构下对应的模版
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
		Integer systemTemplateId = getSystemTemplateIdByActivityFlag(activityFlagEnum);
		List<Template> orgTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
				.eq(Template::getOriginTemplateId, systemTemplateId)
				.eq(Template::getFid, fid)
		);
		if (CollectionUtils.isEmpty(orgTemplates)) {
			return null;
		}
		return orgTemplates.get(orgTemplates.size() - 1);
	}

	/**当templateId存在时，根据templateId查找；否则根据flag查找系统模板
	* @Description
	* @author huxiaolong
	* @Date 2021-07-16 18:11:21
	* @param templateId
	* @param activityFlagEnum
	* @return com.chaoxing.activity.model.Template
	*/
	public Template getTemplateByIdOrActivityFlag(Integer templateId, Activity.ActivityFlagEnum activityFlagEnum) {
		if (templateId == null) {
			List<Template> systemTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
					.eq(Template::getSystem, Boolean.TRUE)
					.eq(Template::getActivityFlag, activityFlagEnum.getValue()));
			return Optional.ofNullable(systemTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
		}
		return templateMapper.selectById(templateId);
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
		if (marketId == null) {
			List<Template> systemTemplates = templateMapper.selectList(new LambdaQueryWrapper<Template>()
					.eq(Template::getSystem, true)
					.eq(Template::getActivityFlag, activityFlagEnum.getValue()));
			return Optional.ofNullable(systemTemplates).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
		}
		return getMarketFirstTemplate(marketId);
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

	/**根据模版id和组件id查询模版组件列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-15 16:08:00
	 * @param templateId
	 * @param componentId
	 * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
	*/
	public List<TemplateComponent> listByTemplateIdAndComponentId(Integer templateId, Integer componentId) {
		return templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
				.eq(TemplateComponent::getTemplateId, templateId)
				.eq(TemplateComponent::getComponentId, componentId)
		);
	}

	/**根据templateComponentId查询子列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-21 18:50:41
	 * @param templateComponentId
	 * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
	*/
	public List<TemplateComponent> listSubTemplateComponent(Integer templateComponentId) {
		return templateComponentMapper.selectList(new LambdaQueryWrapper<TemplateComponent>()
				.eq(TemplateComponent::getPid, templateComponentId));
	}

	/**通过fid，活动标识查询模板，根据模板获取市场id
	* @Description
	* @author huxiaolong
	* @Date 2021-09-01 11:52:12
	* @param fid
	* @param activityFlag
	* @return java.lang.Integer
	*/
	public Integer getMarketIdByTemplate(Integer fid, String activityFlag) {
		Activity.ActivityFlagEnum activityFlagEnum = Activity.ActivityFlagEnum.fromValue(activityFlag);
		if (activityFlagEnum == null) {
			return null;
		}
		Template template = getOrgTemplateByActivityFlag(fid, activityFlagEnum);
		return Optional.ofNullable(template).map(Template::getMarketId).orElse(null);
	}

	/**判断模板是否存在报名组件
	* @Description 
	* @author huxiaolong
	* @Date 2021-09-02 19:41:40
	* @param templateId
	* @return boolean
	*/
    public boolean exitSignUpComponent(Integer templateId) {
    	int count = templateComponentMapper.countTemplateSignUp(templateId);
    	return count > 0;
    }
}