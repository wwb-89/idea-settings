package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.OrgDTO;
import com.chaoxing.activity.dto.manager.WfwContacterDTO;
import com.chaoxing.activity.dto.manager.WfwDepartmentDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
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

	/** 微服务联系人域名 */
	private static final String DOMAIN = "https://contactsyd.chaoxing.com";

	/** 用户有通讯录的机构列表url */
	private static final String GET_USER_HAVE_CONTACTS_ORG_URL = "http://learn.chaoxing.com/apis/roster/getUserUnitList?puid=%s";
	/** 搜索联系人url */
	private static final String SEARCH_CONTACTS_URL = DOMAIN + "/apis/roster/searchRosterUser?puid={uid}&keyword={keyword}&page={page}&pageSize={pageSize}";
	/** 获取部门列表url */
	private static final String GET_DEPARTMENT_URL = DOMAIN + "/apis/dept/getDeptsByServer?type=unit&fid={fid}&puid={uid}&cpage={page}&pageSize={pageSize}";
	/** 获取部门人员列表url */
	private static final String GET_DEPARTMENT_USER_URL = DOMAIN + "/apis/user/getSubDeptUserInfinite?deptId={deptId}&includeSub={includeSub}&cpage={page}&pagesize={pageSize}";

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
			throw new BusinessException(errorMessage);
		}
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
				uids = wfwContacters.stream().map(WfwContacterDTO::getPuid).collect(Collectors.toList());
			}
		} else {
			String errorMessage = jsonObject.getString("errorMsg");
			log.error("departmentId:{}, 查询部门下的联系人error:{}", departmentId, errorMessage);
			throw new BusinessException(errorMessage);
		}
		return uids;
	}

}