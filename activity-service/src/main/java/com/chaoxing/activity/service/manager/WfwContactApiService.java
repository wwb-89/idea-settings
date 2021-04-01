package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.manager.WfwContacterDTO;
import com.chaoxing.activity.dto.manager.WfwDepartmentDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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

	/** 搜索联系人url */
	private static final String SEARCH_CONTACTS_URL = DOMAIN + "/apis/roster/searchRosterUser?puid={uid}&keyword={keyword}&page={page}&pageSize={pageSize}";
	/** 获取部门列表url */
	private static final String GET_DEPARTMENT_URL = DOMAIN + "/apis/dept/getDeptsByServer?fid={fid}&pid={pid}&cpage={page}&pageSize={pageSize}";
	/** 获取部门人员列表url */
	private static final String GET_DEPARTMENT_USER_URL = DOMAIN + "/apis/user/getSubDeptUserInfinite?deptId={deptId}&includeSub={includeSub}&cpage={page}&pagesize={pageSize}";

	@Resource(name = "restTemplateProxy")
	private RestTemplate restTemplate;

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
	 * @param pid
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.chaoxing.activity.dto.manager.WfwDepartmentDTO>
	*/
	public Page<WfwDepartmentDTO> listOrgDepartment(Page<WfwDepartmentDTO> page, Integer fid, Integer pid) {
		String forObject = restTemplate.getForObject(GET_DEPARTMENT_URL, String.class, fid, pid, page.getCurrent(), page.getSize());
		JSONObject jsonObject = JSON.parseObject(forObject);
		Integer result = jsonObject.getInteger("result");
		if (Objects.equals(result, 1)) {
			String wfwContactersJsonStr = jsonObject.getString("msg");
			List<WfwDepartmentDTO> wfwDepartments = JSON.parseArray(wfwContactersJsonStr, WfwDepartmentDTO.class);
			page.setRecords(wfwDepartments);
			return page;
		} else {
			log.error("根据fid:{}, pid:{} 查询部门error:{}", fid, pid);
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

}