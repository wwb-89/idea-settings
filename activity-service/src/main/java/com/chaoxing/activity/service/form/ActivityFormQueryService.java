package com.chaoxing.activity.service.form;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityFormMapper;
import com.chaoxing.activity.model.ActivityForm;
import com.chaoxing.activity.model.FormField;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityFormQueryService
 * @description
 * @blame wwb
 * @date 2020-12-11 21:15:44
 */
@Slf4j
@Service
public class ActivityFormQueryService {

	@Resource
	private ActivityFormMapper activityFormMapper;

	/**查询活动的表单字段列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-11 21:22:28
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.FormField>
	*/
	public List<FormField> listActivityFormField(Integer activityId) {
		List<FormField> result = null;
		List<ActivityForm> activityForms = activityFormMapper.selectList(new QueryWrapper<ActivityForm>()
				.lambda()
				.select(ActivityForm::getFormFieldId)
				.eq(ActivityForm::getActivityId, activityId)
				.orderByAsc(ActivityForm::getSequence)
		);
		if (CollectionUtils.isNotEmpty(activityForms)) {
			List<Integer> formFieldIds = activityForms.stream().map(ActivityForm::getFormFieldId).collect(Collectors.toList());

		} else {
			result = Lists.newArrayList();
		}
		return result;
	}

}
