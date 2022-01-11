package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

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
	public void listUserJoinDepartment() {
		Integer uid = 25418810;
		Integer fid = 117211;
		List<WfwDepartmentDTO> departments = wfwContactApiService.listUserJoinDepartment(uid, fid);
		System.out.println(JSON.toJSONString(departments));
	}

}