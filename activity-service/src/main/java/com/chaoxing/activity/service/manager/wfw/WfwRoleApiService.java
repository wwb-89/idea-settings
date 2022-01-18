package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.wfw.WfwRoleDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**为服务角色api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwRoleApiService
 * @description
 * @blame wwb
 * @date 2022-01-17 15:45:47
 */
@Slf4j
@Service
public class WfwRoleApiService {

	/** 机构角色组url */
	private static final String ORG_ROLE_GROUP_URL = DomainConstant.WFW_ORGANIZATION + "/apis/rolegroup/getrolegroupbyfid?fid=%d&enc=%s&page=%d&pagesize=%d";
	/** 结构角色组下角色url */
	private static final String ORG_ROLE_GROUP_ROLE_URL = DomainConstant.WFW_ORGANIZATION + "/apis/rolegroup/getrolebyfidrolegroupid?rolegroupid=%d&fid=%d&enc=%s&page=%d&pagesize=%d";
	/** 区域下角色url */
	private static final String AREA_ROLE_URL = DomainConstant.WFW_AREA_MANAGE + "/siteInter/getSiteLevelByFwId";

	private static final String KEY = "mic^ruso&ke@y";
	private static final String AREA_KEY = "RE7CT3nt7WO4qcuC";

	private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	private static final DateTimeFormatter AREA_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	private String getEnc(Integer fid) {
		return DigestUtils.md5Hex(fid + KEY + LocalDate.now().format(DATE_FORMAT));
	}

	private String getRoleGroupRoleEnc(Integer roleGroupId, Integer fid) {
		return DigestUtils.md5Hex(fid + roleGroupId + KEY + LocalDate.now().format(DATE_FORMAT));
	}

	private String getAreaEnc(Integer id) {
		return DigestUtils.md5Hex(id + LocalDate.now().format(AREA_DATE_FORMAT) + AREA_KEY);
	}

	/**查询机构下的角色列表（包含组）
	 * @className WfwRoleApiService
	 * @description
	 * @author wwb
	 * @blame wwb
	 * @date 2022-01-17 16:49:43
	 * @version ver 1.0
	 */
	public List<WfwRoleDTO> listFidRole(Integer fid) {
		List<WfwRoleDTO> result = Lists.newArrayList();
		List<WfwRoleDTO> wfwRoleGroups = listFidRoleGroup(fid);
		if (CollectionUtils.isEmpty(wfwRoleGroups)) {
			return Lists.newArrayList();
		}
		for (WfwRoleDTO wfwRoleGroup : wfwRoleGroups) {
			result.add(wfwRoleGroup);
			List<WfwRoleDTO> wfwRoles = listRoleGroupRole(wfwRoleGroup.getId(), fid);
			if (CollectionUtils.isNotEmpty(wfwRoles)) {
				result.addAll(wfwRoles);
			}
		}
		return result;
	}

	private List<WfwRoleDTO> listFidRoleGroup(Integer fid) {
		List<WfwRoleDTO> wfwRoleGroups = Lists.newArrayList();
		String url = String.format(ORG_ROLE_GROUP_URL, fid, getEnc(fid), 1, 100);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (Objects.equals("true", jsonObject.getString("status"))) {
			JSONArray jsonArray = jsonObject.getJSONArray("roleGroupList");
			int size = jsonArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject wfwRoleGroupJsonObject = jsonArray.getJSONObject(i);
				WfwRoleDTO wfwRoleGroup = WfwRoleDTO.builder()
						.id(wfwRoleGroupJsonObject.getInteger("id"))
						.name(wfwRoleGroupJsonObject.getString("groupName"))
						.group(true)
						.build();
				wfwRoleGroups.add(wfwRoleGroup);
			}
		} else {
			String message = jsonObject.getString("msg");
			throw new BusinessException(message);
		}
		return wfwRoleGroups;
	}

	private List<WfwRoleDTO> listRoleGroupRole(Integer roleGroupId, Integer fid) {
		List<WfwRoleDTO> wfwRoles = Lists.newArrayList();
		String url = String.format(ORG_ROLE_GROUP_ROLE_URL, roleGroupId, fid, getRoleGroupRoleEnc(roleGroupId, fid), 1, 100);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (Objects.equals("true", jsonObject.getString("status"))) {
			JSONArray jsonArray = jsonObject.getJSONArray("roleList");
			int size = jsonArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject wfwRoleJsonObject = jsonArray.getJSONObject(i);
				WfwRoleDTO wfwRole = WfwRoleDTO.builder()
						.id(wfwRoleJsonObject.getInteger("id"))
						.role(wfwRoleJsonObject.getInteger("role"))
						.name(wfwRoleJsonObject.getString("roleName"))
						.group(false)
						.roleGroupId(wfwRoleJsonObject.getInteger("roleGroupId"))
						.build();
				wfwRoles.add(wfwRole);
			}
		} else {
			String message = jsonObject.getString("msg");
			throw new BusinessException(message);
		}
		return wfwRoles;
	}

	/**查询区域下的角色列表（不包含组）
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-17 16:50:17
	 * @param fwId
	 * @return java.util.List<com.chaoxing.activity.dto.manager.wfw.WfwRoleDTO>
	*/
	public List<WfwRoleDTO> listAreaRole(Integer fwId) {
		List<WfwRoleDTO> wfwRoles = Lists.newArrayList();
		if (fwId == null) {
			return wfwRoles;
		}
		MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
		params.add("fwId", fwId);
		params.add("enc", getAreaEnc(fwId));
		HttpEntity<MultiValueMap<String, Object>> httpEntity = new HttpEntity<>(params);
		String result = restTemplate.postForObject(AREA_ROLE_URL, httpEntity, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		if (Objects.equals("true", jsonObject.getString("status"))) {
			JSONArray jsonArray = jsonObject.getJSONArray("list");
			int size = jsonArray.size();
			for (int i = 0; i < size; i++) {
				JSONObject wfwRoleJsonObject = jsonArray.getJSONObject(i);
				WfwRoleDTO wfwRole = WfwRoleDTO.builder()
						.id(wfwRoleJsonObject.getInteger("id"))
						.role(wfwRoleJsonObject.getInteger("roleId"))
						.name(wfwRoleJsonObject.getString("roleName"))
						.group(false)
						.build();
				wfwRoles.add(wfwRole);
			}
		} else {
			String message = jsonObject.getString("msg");
			throw new BusinessException(message);
		}
		return wfwRoles;
	}

}