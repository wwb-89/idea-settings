package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.manager.wfw.WfwContacterDTO;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**通讯录api
 * @author wwb
 * @version ver 1.0
 * @className WfwContactApiServiceTests
 * @description
 * @blame wwb
 * @date 2022-01-10 16:40:42
 */
@SpringBootTest
public class WfwContactApiServiceTests {

	@Resource
	private WfwContactApiService wfwContactApiService;

	@Test
	public void search() {
		Page<WfwContacterDTO> page = new Page<>();
		Integer uid = 25418810;
		String sw = "";
		Page<WfwContacterDTO> search = wfwContactApiService.search(page, uid, sw);
		System.out.println(JSON.toJSONString(search));
	}

}
