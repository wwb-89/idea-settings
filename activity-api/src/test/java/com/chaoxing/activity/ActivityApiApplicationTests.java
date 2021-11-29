package com.chaoxing.activity;

import com.chaoxing.activity.dto.activity.create.ActivityCreateFromFormParamDTO;
import com.chaoxing.activity.service.queue.activity.WfwFormSyncActivityQueue;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ActivityApiApplicationTests {
	@Resource
	private WfwFormSyncActivityQueue wfwFormSyncActivityQueue;

	@Test
	void contextLoads() {
	}

	@Test
	public void xxx() {
		wfwFormSyncActivityQueue.push(ActivityCreateFromFormParamDTO.builder().deptId(139378).formId(99082).indexID(97157355).webTemplateId(22).op("data_create").build());
	}

}
