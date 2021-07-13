package com.chaoxing.activity.service.activity.template;

import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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

}