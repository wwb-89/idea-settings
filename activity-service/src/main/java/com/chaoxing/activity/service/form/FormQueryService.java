package com.chaoxing.activity.service.form;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.FormFieldMapper;
import com.chaoxing.activity.model.FormField;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**表单查询服务
 * @author wwb
 * @version ver 1.0
 * @className FormQueryService
 * @description
 * @blame wwb
 * @date 2020-12-11 21:09:35
 */
@Slf4j
@Service
public class FormQueryService {

	@Resource
	private FormFieldMapper formFieldMapper;

	/**查询系统的字段列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-11 21:10:48
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.FormField>
	*/
	public List<FormField> listSystem() {
		List<FormField> formFields = formFieldMapper.selectList(new QueryWrapper<FormField>()
				.lambda()
				.eq(FormField::getSystem, Boolean.TRUE)
				.orderByAsc(FormField::getSequence)
		);
		return formFields;
	}

}