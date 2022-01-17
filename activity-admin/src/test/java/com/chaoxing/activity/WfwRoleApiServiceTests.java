package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.dto.manager.wfw.WfwRoleDTO;
import com.chaoxing.activity.service.manager.wfw.WfwRoleApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**微服务角色
 * @author wwb
 * @version ver 1.0
 * @className WfwRoleApiServiceTests
 * @description
 * @blame wwb
 * @date 2022-01-17 17:42:05
 */
@Slf4j
@SpringBootTest
public class WfwRoleApiServiceTests {

	@Resource
	private WfwRoleApiService wfwRoleApiService;

	@Test
	public void listFidRole() {
		Integer fid = 172057;
		List<WfwRoleDTO> wfwRoles = wfwRoleApiService.listFidRole(fid);
		System.out.println(JSON.toJSONString(wfwRoles));
	}

}
