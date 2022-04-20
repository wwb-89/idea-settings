package com.chaoxing.activity;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className RestTemplateTests
 * @description
 * @blame wwb
 * @date 2022-04-20 11:12:44
 */
@SpringBootTest
public class RestTemplateTests {

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	@Test
	public void test() {
		String url = "http://trojan.wwb.icu:8088/test/timeout";
		String result = restTemplate.getForObject(url, String.class);
		System.out.println(result);
	}

}
