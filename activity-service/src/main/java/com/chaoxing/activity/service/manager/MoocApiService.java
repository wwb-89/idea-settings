package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.MoocUserOrgDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
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
	private static final String USER_MULTI_ORG_URL = "http://mooc1-api.chaoxing.com/gas/person?userid=%d&fields=id,group1,schoolid,roleids,loginname,username&selectuser=true";

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
		if (CollectionUtils.isEmpty(moocUserOrgs)) {
			return moocUserOrgs.stream().map(MoocUserOrgDTO::getFid).collect(Collectors.toList());
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

}