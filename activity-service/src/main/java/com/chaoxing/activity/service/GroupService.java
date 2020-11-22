package com.chaoxing.activity.service;

import com.chaoxing.activity.mapper.GroupAffiliateOrgMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className GroupService
 * @description
 * @blame wwb
 * @date 2020-11-22 13:20:22
 */
@Slf4j
@Service
public class GroupService {

	@Resource
	private GroupAffiliateOrgMapper groupAffiliateOrgMapper;

	/**根据组code查询机构fid列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 13:43:43
	 * @param groupCode
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listGroupFid(String groupCode) {
		List<Integer> fids = groupAffiliateOrgMapper.listFidByGroupCode(groupCode);
		return fids;
	}

}
