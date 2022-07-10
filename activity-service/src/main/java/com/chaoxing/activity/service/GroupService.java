package com.chaoxing.activity.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.GroupAffiliateOrgMapper;
import com.chaoxing.activity.mapper.GroupMapper;
import com.chaoxing.activity.model.Group;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
	@Resource
	private GroupMapper groupMapper;

	/**根据组code查询机构(所属机构)fid
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 13:43:43
	 * @param groupCode
	 * @return java.util.List<java.lang.Integer>
	*/
	public Integer getGroupFid(String groupCode) {
		List<Integer> fids = groupAffiliateOrgMapper.listFidByGroupCode(groupCode);
		return Optional.ofNullable(fids).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**根据code查询组别
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 12:42:55
	 * @param code
	 * @return com.chaoxing.activity.model.Group
	*/
	public Group getByCode(String code) {
		return groupMapper.selectOne(new QueryWrapper<Group>()
			.lambda()
				.eq(Group::getCode, code)
		);
	}

}
