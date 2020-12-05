package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityHandleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class ActivityTaskApplicationTests {

	@Resource
	private ActivityHandleService activityHandleService;

	@Test
	public void syncStatus() {
		activityHandleService.syncStatus();
	}

}
