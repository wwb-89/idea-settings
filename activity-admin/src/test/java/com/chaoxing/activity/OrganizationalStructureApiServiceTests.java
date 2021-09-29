package com.chaoxing.activity;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrganizationalStructureApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-09-23 14:45:59
 */
@SpringBootTest
public class OrganizationalStructureApiServiceTests {

	@Resource
	private OrganizationalStructureApiService organizationalStructureApiService;

	@Test
	public void listUserOrganizationalStructure() {
		Integer uid = 25418810;
		Integer fid = 117211;
		List<String> groupNames = organizationalStructureApiService.listUserFirstGroupNames(uid, fid);
		System.out.println(JSON.toJSON(groupNames));
	}

}