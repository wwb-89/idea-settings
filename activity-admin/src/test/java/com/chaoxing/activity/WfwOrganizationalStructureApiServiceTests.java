package com.chaoxing.activity;

import com.chaoxing.activity.service.manager.wfw.WfwOrganizationalStructureApiService;
import com.google.common.collect.Lists;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwOrganizationalStructureApiServiceTests
 * @description
 * @blame wwb
 * @date 2022-02-14 15:22:02
 */
@SpringBootTest
public class WfwOrganizationalStructureApiServiceTests {

	@Resource
	private WfwOrganizationalStructureApiService wfwOrganizationalStructureApiService;

	@Test
	public void listOrgUid() {
		Integer fid = 117211;
		List<Integer> uids = wfwOrganizationalStructureApiService.listOrgUid(fid);
		System.out.println(uids.size());
	}

	@Test
	public void listOrgSpecifyRoleUid() {
		Integer fid = 117211;
		List<Integer> uids = wfwOrganizationalStructureApiService.listOrgSpecifyRoleUid(fid, Lists.newArrayList(3));
		System.out.println(uids.size());
	}

}
