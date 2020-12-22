package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.sign.SignParticipationDTO;
import com.chaoxing.activity.service.manager.module.SignApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignApiServiceTest
 * @description
 * @blame wwb
 * @date 2020-11-25 20:16:00
 */
@SpringBootTest
public class SignApiServiceTest {

	@Resource
	private SignApiService signApiService;

	@Test
	public void getSignParticipation() {
		SignParticipationDTO signParticipation = signApiService.getSignParticipation(22);
		System.out.println(JSON.toJSON(signParticipation));
	}

}
