package com.chaoxing.activity;

import com.chaoxing.activity.service.ActivityService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityServiceTests
 * @description
 * @blame wwb
 * @date 2020-11-09 20:14:50
 */
@SpringBootTest
public class ActivityServiceTests {

	@Resource
	private ActivityService activityService;

	@Test
	public void list() {
		/*List<Activity> list = activityService.list();
		System.out.println(JSON.toJSONString(list));*/

		System.out.println(activityService.count());
		
	}

}