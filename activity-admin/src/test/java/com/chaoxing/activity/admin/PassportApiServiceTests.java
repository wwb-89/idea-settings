package com.chaoxing.activity.admin;

import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.service.manager.PassportApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className PassportApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-07-18 20:16:29
 */
@SpringBootTest
public class PassportApiServiceTests {

	@Resource
	private PassportApiService passportApiService;

	@Test
	public void getUser() {
		Integer uid = 25418810;
		PassportUserDTO user = passportApiService.getByUid(uid);
		System.out.println(user);
	}

}
