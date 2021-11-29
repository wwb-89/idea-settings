package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.service.manager.wfw.WfwFormApiService;
import com.chaoxing.activity.service.volunteer.VolunteerService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwFormApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-07-12 10:56:01
 */
@SpringBootTest
public class WfwFormApiServiceTests {

	@Resource
	private WfwFormApiService wfwFormApiService;

	@Resource
	private VolunteerService volunteerService;

	@Test
	public void listFormFieldValue() {
		Integer fid = 139378;
		Integer formId = 11916;
		String fieldName = "姓名";
		List<Integer> fieldValues = wfwFormApiService.listFormFieldUid(fid, formId, fieldName);
		Assert.notEmpty(fieldValues, "表单的字段的值不能为空");
	}


	@Test
	public void xxx() {
		volunteerService.listVolunteerServiceType(147004);
	}

	@Test
	public void listData() {
		Integer fid = 180739;
		Integer formId = 121635;
		List<FormDataDTO> formDatas = wfwFormApiService.listFormRecord(formId, fid);
		System.out.println(JSON.toJSONString(formDatas));
	}

}
