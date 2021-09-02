package com.chaoxing.activity;

import com.chaoxing.activity.dto.activity.ActivityFormSyncParamDTO;
import com.chaoxing.activity.service.activity.WfwFormSynOperateQueueService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ActivityApiApplicationTests {
	@Resource
	private WfwFormSynOperateQueueService wfwFormSynOperateQueueService;

	@Test
	void contextLoads() {
	}

	@Test
	public void xxx() {
		wfwFormSynOperateQueueService.addActivityFormSyncOperateTask(ActivityFormSyncParamDTO.builder().deptId(139378).formId(99082).indexID(97157355).webTemplateId(22).op("data_create").build());
	}

}
