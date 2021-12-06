package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.OrgRoleDTO;
import com.chaoxing.activity.dto.manager.uc.UserOrganizationalStructureDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrganizationalStructureApiService
 * @description
 * @blame wwb
 * @date 2021-03-16 10:18:12
 */
@Slf4j
@Service
public class OrganizationalStructureApiService {

	/** 分页获取机构下角色URL */
	private static final String ORG_ROLES_URL = DomainConstant.WFW_ORGANIZATION + "/apis/getrolebyfid?fid=%d&page=%d&pagesize=%d&enc=%s";
	/** 机构下用户列表 */
	private static final String ORG_USERS_URL = DomainConstant.WFW_ORGANIZATION + "/apis/user/getperson?fid=%d&limit=%d&enc=%s";
	/** 机构group下用户列表 */
	private static final String ORG_GROUP_USERS_URL = DomainConstant.WFW_ORGANIZATION + "/apis/user/getperson?fid=%d&%s=%d&limit=%d&enc=%s";
	/** 用户组织架构url（新） */
	private static final String USER_ORGANIZATIONAL_STRUCTURE_NEW_URL = DomainConstant.WFW_ORGANIZATION + "/apis/getuserbyuidfid?fid=%d&uid=%d&showdept=true&enc=%s";
	/** 用户组织架构url（新）key */
	private static final String ENC_KEY = "mic^ruso&ke@y";
	/** 无效的组id */
	private static final Integer INVALID_GROUP_IO = 0;
	/** 无效的组名 */
	private static final String INVALID_GROUP_NAME = "其他";
	private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**获取用户在机构下的组织架构信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-24 11:34:18
	 * @param uid
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.dto.uc.UserOrganizationalStructureDTO>
	*/
	private List<UserOrganizationalStructureDTO> listUserOrganizationalStructure(Integer uid, Integer fid) {
		String enc = getUserOrganizationalStructureEnc(uid, fid);
		String url = String.format(USER_ORGANIZATIONAL_STRUCTURE_NEW_URL, fid, uid, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBoolean("status");
		status = Optional.ofNullable(status).orElse(false);
		if (status) {
			return JSON.parseArray(jsonObject.getString("data"), UserOrganizationalStructureDTO.class);
		} else {
			log.error("根据uid:{}, fid:{} url:{} 获取用户的组织架构信息error", uid, fid, url);
		}
		return null;
	}
	/**获取用户在机构下的组id列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-16 10:44:31
	 * @param uid
	 * @param fid
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listUserGroupId(Integer uid, Integer fid) {
		List<UserOrganizationalStructureDTO> userOrganizationalStructures = listUserOrganizationalStructure(uid, fid);
		if (CollectionUtils.isEmpty(userOrganizationalStructures)) {
			return Lists.newArrayList();
		}
		Set<Integer> groupIdSet = new TreeSet<>();
		for (UserOrganizationalStructureDTO userOrganizationalStructure : userOrganizationalStructures) {
			groupIdSet.addAll(filterEffectiveGroupId(userOrganizationalStructure.getGroup1()));
			groupIdSet.addAll(filterEffectiveGroupId(userOrganizationalStructure.getGroup2()));
			groupIdSet.addAll(filterEffectiveGroupId(userOrganizationalStructure.getGroup3()));
			groupIdSet.addAll(filterEffectiveGroupId(userOrganizationalStructure.getGroup4()));
			groupIdSet.addAll(filterEffectiveGroupId(userOrganizationalStructure.getGroup5()));
		}
		return new ArrayList<>(groupIdSet);
	}

	/**筛选出有效的组id
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-24 11:43:00
	 * @param groups
	 * @return java.util.List<java.lang.Integer>
	*/
	private List<Integer> filterEffectiveGroupId(List<Integer> groups) {
		List<Integer> result;
		if (CollectionUtils.isNotEmpty(groups)) {
			result = groups.stream().filter(groupId -> !Objects.equals(groupId, INVALID_GROUP_IO)).collect(Collectors.toList());
		} else {
			result = Lists.newArrayList();
		}
		return result;
	}

	/**查询用户最下级groupId列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-19 18:30:24
	 * @param uid
	 * @param fid
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listUserMinGroupId(Integer uid, Integer fid) {
		List<UserOrganizationalStructureDTO> userOrganizationalStructures = listUserOrganizationalStructure(uid, fid);
		if (CollectionUtils.isEmpty(userOrganizationalStructures)) {
			return Lists.newArrayList();
		}
		List<Integer> minGroupIds = Lists.newArrayList();
		for (UserOrganizationalStructureDTO userOrganizationalStructure : userOrganizationalStructures) {
			List<Integer> group1 = userOrganizationalStructure.getGroup1();
			// 找出第一个组有几个groupId
			if (CollectionUtils.isNotEmpty(group1)) {
				replaceMinGroupId(group1, userOrganizationalStructure.getGroup2());
				replaceMinGroupId(group1, userOrganizationalStructure.getGroup3());
				replaceMinGroupId(group1, userOrganizationalStructure.getGroup4());
				replaceMinGroupId(group1, userOrganizationalStructure.getGroup5());
				for (Integer groupId : group1) {
					if (!Objects.equals(INVALID_GROUP_IO, groupId)) {
						minGroupIds.add(groupId);
					}
				}
			}
		}
		Set<Integer> minGroupIdSet = new TreeSet<>(minGroupIds);
		return new ArrayList<>(minGroupIdSet);
	}

	/**替换最小组id
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-24 14:11:19
	 * @param minGroupIds
	 * @param groupIds
	 * @return void
	*/
	private void replaceMinGroupId(List<Integer> minGroupIds, List<Integer> groupIds) {
		if (CollectionUtils.isNotEmpty(groupIds)) {
			int minGroupIdsSize = minGroupIds.size();
			int size = groupIds.size();
			int loopSize = Math.min(minGroupIdsSize, size);
			for (int i = 0; i < loopSize; i++) {
				Integer groupId = groupIds.get(i);
				if (!Objects.equals(groupId, INVALID_GROUP_IO)) {
					minGroupIds.set(i, groupId);
				}
			}
		}
	}

	/**获取用户第一个groupName
	 * @Description 格式：信息部/信息部1
	 * @author wwb
	 * @Date 2021-04-25 16:13:59
	 * @param uid
	 * @param fid
	 * @return java.lang.String
	*/
	public String getUserFirstGroupName(Integer uid, Integer fid) {
		List<String> groupNames = listUserFirstGroupNames(uid, fid);
		return String.join(CommonConstant.GROUP_NAME_SEPARATOR, groupNames);
	}
	
	/**查询用户第一组组名
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-25 10:10:58
	 * @param uid
	 * @param fid
	 * @return java.util.List<java.lang.String>
	*/
	public List<String> listUserFirstGroupNames(Integer uid, Integer fid) {
		List<UserOrganizationalStructureDTO> userOrganizationalStructures = listUserOrganizationalStructure(uid, fid);
		if (CollectionUtils.isEmpty(userOrganizationalStructures)) {
			return Lists.newArrayList();
		}
		List<String> firstGoupNames = Lists.newArrayList();
		UserOrganizationalStructureDTO userOrganizationalStructure = userOrganizationalStructures.get(0);
		List<String> group1Name = userOrganizationalStructure.getGroup1Name();
		if (CollectionUtils.isNotEmpty(group1Name)) {
			int firstEffectiveGroupNameIndex = -1;
			int size = group1Name.size();
			for (int i = 0; i < size; i++) {
				if (!Objects.equals(group1Name.get(i), INVALID_GROUP_NAME)) {
					firstEffectiveGroupNameIndex = i;
					break;
				}
			}
			if (firstEffectiveGroupNameIndex > -1) {
				firstGoupNames.add(group1Name.get(firstEffectiveGroupNameIndex));
				String group2EffectiveName = getEffectiveGroupName(userOrganizationalStructure.getGroup2Name(), firstEffectiveGroupNameIndex);
				if (StringUtils.isNotBlank(group2EffectiveName)) {
					firstGoupNames.add(group2EffectiveName);
					String group3EffectiveName = getEffectiveGroupName(userOrganizationalStructure.getGroup3Name(), firstEffectiveGroupNameIndex);
					if (StringUtils.isNotBlank(group3EffectiveName)) {
						firstGoupNames.add(group3EffectiveName);
						String group4EffectiveName = getEffectiveGroupName(userOrganizationalStructure.getGroup4Name(), firstEffectiveGroupNameIndex);
						if (StringUtils.isNotBlank(group4EffectiveName)) {
							firstGoupNames.add(group4EffectiveName);
							String group5EffectiveName = getEffectiveGroupName(userOrganizationalStructure.getGroup5Name(), firstEffectiveGroupNameIndex);
							if (StringUtils.isNotBlank(group5EffectiveName)) {
								firstGoupNames.add(group5EffectiveName);
							}
						}
					}
				}
			}
		}
		return firstGoupNames;
	}

	/**获取用户的学号
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-24 17:52:32
	 * @param uid
	 * @param fid
	 * @return java.lang.String
	*/
	@Cacheable(value = CacheConstant.CACHE_KEY_PREFIX + "user_student_no")
	public String getUserStudentNo(Integer uid, Integer fid) {
		String studentNo = "";
		List<UserOrganizationalStructureDTO> userOrganizationalStructures = listUserOrganizationalStructure(uid, fid);
		if (CollectionUtils.isNotEmpty(userOrganizationalStructures)) {
			UserOrganizationalStructureDTO userOrganizationalStructure = userOrganizationalStructures.get(0);
			String aliasName = userOrganizationalStructure.getAliasName();
			if (StringUtils.isNotBlank(aliasName)) {
				studentNo = aliasName;
			}
		}
		return studentNo;
	}

	/**获取有效的组名称
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-24 14:22:00
	 * @param groupNames
	 * @param index
	 * @return java.lang.String
	*/
	private String getEffectiveGroupName(List<String> groupNames, int index) {
		String result = null;
		if (CollectionUtils.isNotEmpty(groupNames) && groupNames.size() > index) {
			String groupName = groupNames.get(index);
			if (!Objects.equals(INVALID_GROUP_NAME, groupName)) {
				result = groupName;
			}
		}
		return result;
	}

	/**获取enc
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-25 10:13:41
	 * @param uid
	 * @param fid
	 * @return java.lang.String
	*/
	private String getUserOrganizationalStructureEnc(Integer uid, Integer fid) {
		LocalDate now = LocalDate.now();
		return DigestUtils.md5Hex("" + fid + uid + ENC_KEY + now.format(DATE_TIME_FORMATTER));
	}

	private String getOrgUsersEnc(Integer fid, Integer limit) {
		return DigestUtils.md5Hex("fid=" + fid + "&limit=" + limit + "mic^ro&deke@y");
	}

	private String getOrgGroupUsersEnc(Integer fid, String key, Integer value, Integer limit) {
		return DigestUtils.md5Hex("fid=" + fid + "&" + key + "=" + value + "&limit=" + limit + "mic^ro&deke@y");
	}

	private String getOrgRoleEnc(Integer fid) {
		return DigestUtils.md5Hex(fid + ENC_KEY + LocalDate.now().format(DATE_TIME_FORMATTER));
	}

	/**查询机构下的uid列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-28 16:49:23
	 * @param fid
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listOrgUid(Integer fid) {
		List<Integer> uids =Lists.newArrayList();
		String enc = getOrgUsersEnc(fid, Integer.MAX_VALUE);
		String url = String.format(ORG_USERS_URL, fid, Integer.MAX_VALUE, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBoolean("status");
		status = Optional.ofNullable(status).orElse(false);
		if (status) {
			JSONArray json = jsonObject.getJSONArray("json");
			int size = json.size();
			if (size > 0) {
				Set<Integer> uidSet = new HashSet<>();
				for (int i = 0; i < size; i++) {
					JSONObject user = json.getJSONObject(i);
					uidSet.add(user.getInteger("userid"));
				}
				uids = new ArrayList<>(uidSet);
			}
		}
		return uids;
	}

	public List<Integer> listOrgGroupUid(Integer fid, Integer groupId, Integer groupLevel) {
		List<Integer> uids =Lists.newArrayList();
		String key = "group5";
		if (Objects.equals(1, groupLevel)) {
			key = "group1";
		} else if (Objects.equals(2, groupLevel)) {
			key = "group2";
		} else if (Objects.equals(3, groupLevel)) {
			key = "group3";
		} else if (Objects.equals(4, groupLevel)) {
			key = "group4";
		}
		String enc = getOrgGroupUsersEnc(fid, key, groupId, Integer.MAX_VALUE);
		String url = String.format(ORG_GROUP_USERS_URL, fid, key, groupId, Integer.MAX_VALUE, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBoolean("status");
		status = Optional.ofNullable(status).orElse(false);
		if (status) {
			JSONArray json = jsonObject.getJSONArray("json");
			int size = json.size();
			if (size > 0) {
				Set<Integer> uidSet = new HashSet<>();
				for (int i = 0; i < size; i++) {
					JSONObject user = json.getJSONObject(i);
					uidSet.add(user.getInteger("userid"));
				}
				uids = new ArrayList<>(uidSet);
			}
		}
		return uids;
	}

	/**查询机构fid下的角色列表
	* @Description 
	* @author huxiaolong
	* @Date 2021-06-02 11:34:06
	* @param fid
	* @return java.util.List<com.chaoxing.activity.dto.OrgRoleDTO>
	*/
	public List<OrgRoleDTO> listOrgRoles(Integer fid) {
		String enc = getOrgRoleEnc(fid);
		String url = String.format(ORG_ROLES_URL, fid, 1, Integer.MAX_VALUE, enc);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		boolean status = jsonObject.getBoolean("status");
		if (status) {
			return JSON.parseArray(jsonObject.getString("data"), OrgRoleDTO.class);
		} else {
			log.error("根据fid:{}, url:{} 获取机构的角色信息error", fid, url);
		}
		return null;
	}

}