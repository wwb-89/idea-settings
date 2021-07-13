package com.chaoxing.activity.service.activity.template;

import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Template;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

}