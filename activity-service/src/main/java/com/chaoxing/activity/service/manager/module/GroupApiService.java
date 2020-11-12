package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.GroupDTO;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**小组服务
 * @author wwb
 * @version ver 1.0
 * @className GroupApiService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:35:16
 */
@Slf4j
@Service
public class GroupApiService {

	private static final String GROUP_CREATE_URL = "http://group.yd.chaoxing.com/apis/school/circle_addCircle";
	private static final String GROUP_JOIN_URL = "http://group.yd.chaoxing.com/apis/school/cmem_joinCircle";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**创建小组
	 * @Description
	 * @author wwb
	 * @Date 2019-10-23 12:52:31
	 * @param uid 创建人id
	 * @param groupName 小组名称
	 * @param autoJoinUids 自动加入小组的用户id列表
	 * @return com.chaoxing.activity.dto.GroupDTO
	 */
	public GroupDTO create(Integer uid, String groupName, List<Integer> autoJoinUids) {
		createParamValidate(uid, groupName);
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("uid", String.valueOf(uid));
		params.add("name", groupName);
		if (CollectionUtils.isNotEmpty(autoJoinUids)) {
			params.add("uids", String.join(CommonConstant.DEFAULT_SEPARATOR, autoJoinUids.stream().map(String::valueOf).collect(Collectors.toList())));
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		String result = restTemplate.postForObject(GROUP_CREATE_URL, request, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("result");
		if (code.equals(1)) {
			jsonObject = jsonObject.getJSONObject("data");
			return GroupDTO.builder()
					.id(jsonObject.getInteger("id"))
					.bbsid(jsonObject.getString("bbsid"))
					.build();
		}
		throw new BusinessException(jsonObject.getString("msg"));
	}
	/**创建小组参数验证
	 * @Description
	 * @author wwb
	 * @Date 2019-10-23 11:39:13
	 * @param uid
	 * @param groupName
	 * @return void
	 */
	private void createParamValidate(Integer uid, String groupName) {
		if (uid == null) {
			throw new BusinessException("小组创建人id不能为空");
		}
		if (StringUtils.isEmpty(groupName)) {
			throw new BusinessException("小组名称不能为空");
		}
	}
	/**加入小组
	 * @Description
	 * @author wwb
	 * @Date 2019-11-14 18:35:12
	 * @param groupId
	 * @param uid
	 * @return void
	 */
	public void joinGroup(Integer groupId, Integer uid) {
		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("circleId", String.valueOf(groupId));
		params.add("uid", String.valueOf(uid));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
		String result = restTemplate.postForObject(GROUP_JOIN_URL, request, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer code = jsonObject.getInteger("result");
		if (!code.equals(1)) {
			log.warn("用户:{} 加入小组:{} 失败，msg:{}", uid, groupId, jsonObject.getString("errorMsg"));
		}
	}

}