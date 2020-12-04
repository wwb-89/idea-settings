package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.CloudApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;

/**
 * @author wwb
 * @version ver 1.0
 * @className CloudApiServiceTests
 * @description
 * @blame wwb
 * @date 2020-11-10 20:19:24
 */
@SpringBootTest
public class CloudApiServiceTests {

	@Resource
	private CloudApiService cloudApiService;

	@Test
	public void upload() {
		File file = new File("/Users/wwb/Downloads/6015.jpg_wh300.jpg");
		String ip = "171.212.208.156";
		String upload = cloudApiService.upload(file, ip);
		System.out.println(upload);
	}

}