package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.service.manager.wfw.WfwContactApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Comparator;
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
		Integer uid = 58488158;
		Integer fid = 170642;
		List<WfwDepartmentDTO> departments = wfwContactApiService.listUserJoinDepartment(uid, fid);
		departments.sort(Comparator.comparing(WfwDepartmentDTO::getLevel).reversed());
		System.out.println(JSON.toJSONString(departments.get(0)));
	}

	@Test
	public void getUserDepartment() {
		Integer uid = 58488158;
		Integer fid = 170642;
		WfwDepartmentDTO userDepartment = wfwContactApiService.getUserDepartment(uid, fid);
		System.out.println(JSON.toJSONString(userDepartment));
	}

}