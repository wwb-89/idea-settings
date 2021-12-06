package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.MoocUserOrgDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className MoocApiService
 * @description
 * @blame wwb
 * @date 2020-11-12 14:14:24
 */
@Slf4j
@Service
public class MoocApiService {

	/** 用户多机构信息url */
	private static final String USER_MULTI_ORG_URL = DomainConstant.MOOC_API + "/gas/person?userid=%d&fields=id,group1,schoolid,roleids,loginname,username&selectuser=true";

	/** 用户角色信息URL */
	private static final String USER_ROLE_URL = DomainConstant.MOOC + "/gas/person?userid=%d&fields=schoolid,roleids,loginname,username,id,status,iscertify,aliasname,group1,group2,group3&fid=%d";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplateProxy;

	/**查询用户的机构id列表
	 * @Description
	 * @author wwb
	 * @Date 2019-10-23 09:56:45
	 * @param uid
	 * @return java.util.List<java.lang.Integer>
	 */
	public List<Integer> listUserFids(Integer uid) {
		List<MoocUserOrgDTO> moocUserOrgs = listMoocUserOrgResult(uid);
		if (CollectionUtils.isNotEmpty(moocUserOrgs)) {
			List<Integer> fids = moocUserOrgs.stream().map(MoocUserOrgDTO::getFid).collect(Collectors.toList());
			Collections.reverse(fids);
			return fids;
		} else {
			return new ArrayList<>();
		}
	}
	/**获取用户姓名
	 * @Description
	 * @author wwb
	 * @Date 2019-10-29 14:05:34
	 * @param uid
	 * @return java.lang.String
	 */
	public String getUserRealName(Integer uid) {
		List<MoocUserOrgDTO> moocUserOrgs = listMoocUserOrgResult(uid);
		if (CollectionUtils.isNotEmpty(moocUserOrgs)) {
			return moocUserOrgs.get(0).getRealName();
		} else {
			return null;
		}
	}

	private List<MoocUserOrgDTO> listMoocUserOrgResult(Integer uid) {
		String url = String.format(USER_MULTI_ORG_URL, uid);
		String result = restTemplateProxy.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		String data = jsonObject.getString("data");
		return JSON.parseArray(data, MoocUserOrgDTO.class);
	}

	/**根据用户fid 、 uid 获取用户角色id集合
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-03 10:46:53
	 * @param fid
	 * @param uid
	 * @return com.chaoxing.activity.dto.manager.MoocUserOrgDTO
	 */
	public List<Integer> getUserRoleIds(Integer fid, Integer uid) {
		MoocUserOrgDTO moocUser = getUserRoleInfo(fid, uid);
		if (moocUser == null || StringUtils.isEmpty(moocUser.getRoleIds())) {
			return Lists.newArrayList();
		}

		List<String> roleIdSplits = Arrays.asList(moocUser.getRoleIds().split(","));
		List<Integer> roleIds = Lists.newArrayList();
		CollectionUtils.collect(roleIdSplits, Integer::valueOf, roleIds);
		return roleIds;
	}

	/**根据uid、fid查询用户信息
	* @Description
	* @author huxiaolong
	* @Date 2021-06-03 10:46:53
	* @param fid
	* @param uid
	* @return com.chaoxing.activity.dto.manager.MoocUserOrgDTO
	*/
	private MoocUserOrgDTO getUserRoleInfo(Integer fid, Integer uid) {
		String url = String.format(USER_ROLE_URL, uid, fid);
		String result = restTemplateProxy.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		String data = jsonObject.getString("data");
		List<MoocUserOrgDTO> moocUserList = JSON.parseArray(data, MoocUserOrgDTO.class);
		if (CollectionUtils.isNotEmpty(moocUserList)) {
			return moocUserList.get(0);
		}
		return null;
	}

}