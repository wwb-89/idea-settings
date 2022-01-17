package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.google.common.collect.Maps;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.io.File;
import java.util.Map;
import java.util.TreeMap;

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
		TreeMap<String, String> treeMap = Maps.newTreeMap();
		for (File file : files) {
			String upload = cloudApiService.upload(file, ip);
			String objectid = JSON.parseObject(upload).getString("objectid");
//			System.out.println(file + objectid);
			treeMap.put(file.getName(), objectid);
		}

		for (Map.Entry<String, String> entry : treeMap.entrySet()) {
			System.out.println(entry.getKey() + " ====== " + entry.getValue());
		}
	}

	@Test
	public void getInfo() {
		String cloudId = "68065603fbcb805725f7ef5e21cef03c";
		cloudApiService.getImage(cloudId);
	}

}