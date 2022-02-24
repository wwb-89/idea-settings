package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.XxtNoticeApiService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className XxtNoticeApiServiceTests
 * @description
 * @blame wwb
 * @date 2022-02-24 19:26:35
 */
@SpringBootTest
public class XxtNoticeApiServiceTests {

	@Resource
	private XxtNoticeApiService xxtNoticeApiService;

	@Test
	public void sendNotice() {
		String title = "测试参数错误的问题";
		String content = "测试参数错误的问题";
		xxtNoticeApiService.sendNotice(title, content, "", 168054129, Lists.newArrayList(25418810));

	}

}
