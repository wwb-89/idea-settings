package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
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
		File file = new File("/Users/wwb/Desktop/activity_release_cover.png");
		String ip = "171.212.208.156";
		String upload = cloudApiService.upload(file, ip);
		System.out.println(upload);
	}

	@Test
	public void folderUpload() {
		File folder = new File("/Users/wwb/Downloads/图片库更新");
		String ip = "171.212.208.156";
		File[] files = folder.listFiles(file -> file.getName().endsWith(".png"));
		for (File file : files) {
			String upload = cloudApiService.upload(file, ip);
			String objectid = JSON.parseObject(upload).getString("objectid");
			System.out.println(objectid);
		}
	}

}