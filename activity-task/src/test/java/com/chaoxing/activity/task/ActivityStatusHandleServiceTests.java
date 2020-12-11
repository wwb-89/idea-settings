package com.chaoxing.activity.task;

import com.chaoxing.activity.service.activity.ActivityStatusHandleService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatusHandleServiceTests
 * @description
 * @blame wwb
 * @date 2020-12-11 14:57:25
 */
@SpringBootTest
public class ActivityStatusHandleServiceTests {

	@Resource
	private ActivityStatusHandleService activityStatusHandleService;

	@Test
	public void syncStatus() {
		activityStatusHandleService.startStatusSync();
		activityStatusHandleService.endStatusSync();
	}

}
