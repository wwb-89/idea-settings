package com.chaoxing.activity;

import com.chaoxing.activity.service.activity.manager.ActivityPushReminderService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityPushReminderServiceTests
 * @description
 * @blame wwb
 * @date 2022-02-24 19:08:43
 */
@SpringBootTest
public class ActivityPushReminderServiceTests {

	@Resource
	private ActivityPushReminderService activityPushReminderService;

	@Test
	public void sendNotice() {
		Integer activityId = 2775665;
		activityPushReminderService.sendNotice(activityId);
	}

}