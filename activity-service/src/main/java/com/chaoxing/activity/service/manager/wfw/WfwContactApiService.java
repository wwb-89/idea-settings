package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwContacterDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO;
import com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO;
import com.chaoxing.activity.util.constant.CacheConstant;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**微服务通讯录api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwContactsApiService
 * @description
 * @blame wwb
 * @date 2021-03-28 11:16:39
 */
@Slf4j
@Service
public class WfwContactApiService {

	/** 用户有通讯录的机构列表url */
	private static final String GET_USER_HAVE_CONTACTS_ORG_URL = DomainConstant.LEARN + "/apis/roster/getUserUnitList?puid=%s";
	/** 搜索联系人url */
	private static final String SEARCH_CONTACTS_URL = DomainConstant.WFW_CONTACTS + "/apis/roster/searchRosterUser?puid={uid}&keyword={keyword}&page={page}&pageSize={pageSize}";
	/** 获取部门列表url */
	private static final String GET_DEPARTMENT_URL = DomainConstant.WFW_CONTACTS + "/apis/dept/getDeptsByServer?type=unit&fid={fid}&puid={uid}&cpage={page}&pageSize={pageSize}";
	/** 获取机构下部门列表url */
	private static final String GET_ORG_DEPARTMENT_URL = DomainConstant.WFW_CONTACTS + "/apis/dept/getDeptsInfoByFidAndName4Server?fid={fid}&offsetValue={offsetValue}";
	/** 获取部门人员列表url */
	private static final String GET_DEPARTMENT_USER_URL = DomainConstant.WFW_CONTACTS + "/apis/user/getSubDeptUserInfinite?deptId={deptId}&includeSub={includeSub}&cpage={page}&pagesize={pageSize}";
	/** 根据管理员puid获取管理员管理的部门 */
	private static final String GET_DEPARTMENT_BY_MANAGER_URL = DomainConstant.WFW_CONTACTS + "/apis/dept/getDeptsByManager?fid={fid}&puid={puid}";
	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

	/**用户有通讯录的机构列表
	 * @Description
	 * @author wwb
	 * @Date 2021-04-12 17:10:16
	 * @param uid
	 * @return java.util.List<com.chaoxing.activity.dto.OrgDTO>
	*/
	public List<OrgDTO> listUserHaveContactsOrg(Integer uid) {
		String url = String.format(GET_USER_HAVE_CONTACTS_ORG_URL, uid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Integer status = jsonObject.getInteger("result");
 		if (Objects.equals(status, 1)) {
			return JSON.parseArray(jsonObject.getString("msg"), OrgDTO.class);
		} else {
			String errorMessage = jsonObject.getString("errorMsg");
			log.error("根据uid:{} 查询有通讯录的机构列表error:{}", uid, errorMessage);
			return Lists.newArrayList();
		}
	}

	/**通讯录架构下的机构部门信息
	* @Description
	* @author huxiaolong
	* @Date 2021-06-17 10:45:52
	* @param fid
	* @return java.util.List<com.chaoxing.activity.dto.OrgDTO>
	*/
	@Cacheable(value = CacheConstant.CACHE_KEY_PREFIX + "org_contacts")
	public List<WfwGroupDTO> listUserContactOrgsByFid(Integer fid) {
		List<WfwDepartmentDTO> result = listOrgDepartment(fid, null);
		return convert2WfwGroup(result);
	}

	/**查询所有下级
	 * @Description
	 * @author wwb
	 * @Date 2021-11-08 19:00:12
	 * @param wfwGroup
	 * @param wfwGroups
	 * @return java.util.List<com.chaoxing.activity.dto.manager.wfw.WfwGroupDTO>
	*/
	public List<WfwGroupDTO> listAllSubWfwGroups(WfwGroupDTO wfwGroup, List<WfwGroupDTO> wfwGroups) {
		List<WfwGroupDTO> children = Lists.newArrayList();
		if (wfwGroup == null || CollectionUtils.isEmpty(wfwGroups)) {
			return children;
		}
		for (WfwGroupDTO group : wfwGroups) {
			if (Objects.equals(group.getGid(), wfwGroup.getId())) {
				children.add(group);
				children.addAll(listAllSubWfwGroups(group, wfwGroups));
			}
		}
		return children;
	}

	/**deptment转换成wfwGroup数据结构
	* @Description
	* @author huxiaolong
	* @Date 2021-06-17 11:45:28
	* @param departments
	* @return java.util.List<com.chaoxing.activity.dto.manager.WfwGroupDTO>
	*/
	private List<WfwGroupDTO> convert2WfwGroup(List<WfwDepartmentDTO> departments) {
		List<WfwGroupDTO> result = Lists.newArrayList();
		if (CollectionUtils.isEmpty(departments)) {
			return result;
		}
		List<WfwGroupDTO> wfwGroups = Lists.newArrayList();
		Map<String, Integer> deptChildNumMap = Maps.newHashMap();
		for (WfwDepartmentDTO dept : departments) {
			String groupId = dept.getId().toString();
			String gid = dept.getPid() == null ? null : dept.getPid().toString();
			wfwGroups.add(WfwGroupDTO.builder()
					.id(groupId)
					.gid(gid)
					.virtualId(String.valueOf(dept.getId()))
					.groupname(dept.getName())
					.groupLevel(dept.getLevel())
					.build());
			if (StringUtils.isNotBlank(gid)) {
				Integer soncount = Optional.ofNullable(deptChildNumMap.get(gid)).orElse(0);
				deptChildNumMap.put(gid, soncount + 1);
			}
		}

		for (WfwGroupDTO group : wfwGroups) {
			Integer soncount = Optional.ofNullable(deptChildNumMap.get(group.getId())).orElse(0);
			group.setSoncount(soncount);
			result.add(group);
		}
		return result;
	}

	/**搜索联系人
	 * @Description
	 * @author wwb
	 * @Date 2021-03-28 12:23:26
	 * @param page
	 * @param uid
	 * @param sw
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.manager.WfwContacterDTO>
	*/
	public Page<WfwContacterDTO> search(Page<WfwContacterDTO> page, Integer uid, String sw) {
		String forObject = restTemplate.getForObject(SEARCH_CONTACTS_URL, String.class, uid, sw, page.getCurrent(), page.getSize());
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		if (Objects.equals(result, 1)) {
			String wfwContactersJsonStr = jsonObject.getJSONObject("data").getString("list");
			List<WfwContacterDTO> wfwContacters = JSON.parseArray(wfwContactersJsonStr, WfwContacterDTO.class);
			page.setRecords(wfwContacters);
			return page;
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("根据uid:{}, sw:{} 搜索联系人error:{}", uid, sw, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**查询机构下级部门
	 * @Description
	 * @author wwb
	 * @Date 2021-03-28 16:59:08
	 * @param page
	 * @param fid
	 * @param uid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.manager.WfwDepartmentDTO>
	*/
	public Page<WfwDepartmentDTO> listOrgDepartment(Page<WfwDepartmentDTO> page, Integer fid, Integer uid) {
		String forObject = restTemplate.getForObject(GET_DEPARTMENT_URL, String.class, fid, uid, page.getCurrent(), page.getSize());
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		if (Objects.equals(result, 1)) {
			String wfwContactersJsonStr = jsonObject.getString("msg");
			List<WfwDepartmentDTO> wfwDepartments = JSON.parseArray(wfwContactersJsonStr, WfwDepartmentDTO.class);
			page.setRecords(wfwDepartments);
			return page;
		} else {
			log.error("根据fid:{}, uid:{} 查询部门error:{}", fid, uid);
			throw new BusinessException("查询部门列表失败");
		}
	}

	/**查询用户加入的机构部门列表
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-11 11:08:45
	 * @param uid
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO>
	*/
	public List<WfwDepartmentDTO> listUserJoinDepartment(Integer uid, Integer fid) {
		String forObject = restTemplate.getForObject(GET_DEPARTMENT_URL, String.class, fid, uid, 1, Integer.MAX_VALUE);
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		if (Objects.equals(result, 1)) {
			String wfwContactersJsonStr = jsonObject.getString("msg");
			List<WfwDepartmentDTO> wfwDepartments = JSON.parseArray(wfwContactersJsonStr, WfwDepartmentDTO.class);
			// 过滤上级部门
			if (wfwDepartments == null) {
				wfwDepartments = Lists.newArrayList();
			}
			List<Integer> pids = wfwDepartments.stream().map(WfwDepartmentDTO::getPid).collect(Collectors.toList());
			return wfwDepartments.stream().filter(v -> !pids.contains(v.getId())).collect(Collectors.toList());
		} else {
			log.error("根据uid:{}, fid:{} 查询用户加入的部门error", uid, fid);
			throw new BusinessException("查询部门列表失败");
		}
	}

	/**查询用户的部门（多个部门获取第一个）
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-11 11:14:56
	 * @param uid
	 * @param fid
	 * @return com.chaoxing.activity.dto.manager.wfw.WfwDepartmentDTO
	*/
	public WfwDepartmentDTO getUserDepartment(Integer uid, Integer fid) {
		List<WfwDepartmentDTO> wfwDepartments = listUserJoinDepartment(uid, fid);
		return wfwDepartments.stream().findFirst().orElse(null);

	}

	/**查询机构下级部门
	* @Description
	* @author huxiaolong
	* @Date 2021-06-22 10:15:23
	* @param fid
	* @param offsetValue
	* @return java.util.List<com.chaoxing.activity.dto.manager.WfwDepartmentDTO>
	*/
	public List<WfwDepartmentDTO> listOrgDepartment(Integer fid, Long offsetValue) {
		List<WfwDepartmentDTO> departments = Lists.newArrayList();
		while (true) {
			String forObject = restTemplate.getForObject(GET_ORG_DEPARTMENT_URL, String.class, fid, offsetValue);
			JSONObject jsonObject = JSON.parseObject(forObject);
			Integer result = jsonObject.getInteger("result");
			if (Objects.equals(result, 1)) {
				JSONObject dataObj = jsonObject.getJSONObject("data");
				List<WfwDepartmentDTO> tempResult = JSON.parseArray(dataObj.getString("list"), WfwDepartmentDTO.class);
				if (CollectionUtils.isEmpty(tempResult)) {
					break;
				}
				departments.addAll(tempResult);
				offsetValue = dataObj.getLongValue("lastValue");
			} else {
				log.error("根据fid:{} 查询部门error:{}", fid);
				throw new BusinessException("查询部门列表失败");
			}
		}
		return departments;
	}

	/**查询部门下的联系人
	 * @Description
	 * @author wwb
	 * @Date 2021-03-28 17:19:32
	 * @param page
	 * @param departmentId
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.manager.WfwContacterDTO>
	*/
	public Page<WfwContacterDTO> listDepartmentContacter(Page<WfwContacterDTO> page, Integer departmentId) {
		String forObject = restTemplate.getForObject(GET_DEPARTMENT_USER_URL, String.class, departmentId, 0, page.getCurrent(), page.getSize());
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		if (Objects.equals(result, 1)) {
			String wfwContactersJsonStr = jsonObject.getJSONObject("data").getString("list");
			List<WfwContacterDTO> wfwContacters = JSON.parseArray(wfwContactersJsonStr, WfwContacterDTO.class);
			page.setTotal(jsonObject.getJSONObject("data").getInteger("allCount"));
			page.setRecords(wfwContacters);
			return page;
		} else {
			String errorMessage = jsonObject.getString("msg");
			log.error("departmentId:{}, 查询部门下的联系人error:{}", departmentId, errorMessage);
			throw new BusinessException(errorMessage);
		}
	}

	/**查询部门下的用户id列表
	 * @Description
	 * @author wwb
	 * @Date 2021-05-28 16:43:51
	 * @param departmentId
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listDepartmentUid(Integer departmentId) {
		List<Integer> uids = Lists.newArrayList();
		String forObject = restTemplate.getForObject(GET_DEPARTMENT_USER_URL, String.class, departmentId, 0, 1, Integer.MAX_VALUE);
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		if (Objects.equals(result, 1)) {
			String wfwContactersJsonStr = jsonObject.getJSONObject("data").getString("list");
			List<WfwContacterDTO> wfwContacters = JSON.parseArray(wfwContactersJsonStr, WfwContacterDTO.class);
			if (CollectionUtils.isNotEmpty(wfwContacters)) {
				uids = wfwContacters.stream().map(WfwContacterDTO::getPuid).filter(v -> v != null).collect(Collectors.toList());
			}
		} else {
			String errorMessage = jsonObject.getString("errorMsg");
			log.error("departmentId:{}, 查询部门下的联系人error:{}", departmentId, errorMessage);
			throw new BusinessException(errorMessage);
		}
		return uids;
	}

	/**根据管理员puid获取管理员管理的部门
	* @Description
	* @author huxiaolong
	* @Date 2021-06-16 10:15:19
	* @param fid
	* @param puid
	* @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.manager.WfwContacterDTO>
	*/
	public List<WfwDepartmentDTO> listManagerDepartment(Integer fid, Integer puid) {
		String forObject = restTemplate.getForObject(GET_DEPARTMENT_BY_MANAGER_URL, String.class, fid, puid);
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		String msg = jsonObject.getString("msg");
		if (Objects.equals(result, 1)) {
			JSONObject dataObj = jsonObject.getJSONObject("data");
			return JSON.parseArray(dataObj.getString("list"), WfwDepartmentDTO.class);
		} else {
			log.error("根据fid:{}, uid:{} 查询管理下的部门error:{}", fid, puid, msg);
			throw new BusinessException("查询管理下的部门列表失败");
		}
	}

}