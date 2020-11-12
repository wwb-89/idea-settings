package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyHandleService;
import com.chaoxing.activity.service.activity.classify.ActivityClassifyQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyServiceTest
 * @description
 * @blame wwb
 * @date 2020-11-10 17:39:29
 */
@SpringBootTest
public class ActivityClassifyServiceTests {

	@Resource
	private ActivityClassifyHandleService activityClassifyHandleService;
	@Resource
	private ActivityClassifyQueryService activityClassifyQueryService;

	@Test
	public void add() {
		ActivityClassify activityClassify = ActivityClassify.builder()
				.name("")
				.build();
		LoginUserDTO loginUser = LoginUserDTO.builder()
				.fid(1385)
				.build();
		activityClassifyHandleService.add(activityClassify, loginUser);
	}

	@Test
	public void edit() {
		ActivityClassify activityClassify = ActivityClassify.builder()
				.id(11)
				.name("活动11")
				.build();
		LoginUserDTO loginUser = LoginUserDTO.builder()
				.fid(1385)
				.build();
		activityClassifyHandleService.edit(activityClassify, loginUser);
	}

	@Test
	public void delete() {
		LoginUserDTO loginUser = LoginUserDTO.builder()
				.fid(1385)
				.build();
		activityClassifyHandleService.delete(10, loginUser);
	}

	@Test
	public void list() {
		List<ActivityClassify> activityClassifies = activityClassifyQueryService.listSystem();
		System.out.println(JSON.toJSONString(activityClassifies));
	}

}
