package com.chaoxing.activity;

import com.chaoxing.activity.service.data.OrgActivityDataFormPushService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgActivityDataFormPushServiceTests
 * @description
 * @blame wwb
 * @date 2022-02-21 15:39:45
 */
@SpringBootTest
public class OrgActivityDataFormPushServiceTests {

	@Resource
	private OrgActivityDataFormPushService orgActivityDataFormPushService;

	@Test
	public void push() {
		Integer activityId = 2775630;
		orgActivityDataFormPushService.push(activityId);
	}

}