package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.UserExtraInfoDTO;
import com.chaoxing.activity.service.manager.UcApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UcApiServiceTests
 * @description
 * @blame wwb
 * @date 2022-03-15 10:23:40
 */
@SpringBootTest
public class UcApiServiceTests {

	@Resource
	private UcApiService ucApiService;

	@Test
	public void getUserExtraInfoByFidAndUid() {
		Integer uid = 25418810;
		Integer fid = 117211;
		UserExtraInfoDTO userExtraInfo = ucApiService.getUserExtraInfoByFidAndUid(fid, uid);
		System.out.println(JSON.toJSONString(userExtraInfo));
	}

}
