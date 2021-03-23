package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.Pagination;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;

/** 微服务联系人
 * @className WfwContactApiService
 * @description 
 * @author wwb
 * @blame wwb
 * @date 2021-03-23 16:01:59
 * @version ver 1.0
 */
@Slf4j
@Service
public class WfwContactApiService {

    /** 微服务联系人域名 */
    private static final String DOMAIN = "https://contactsyd.chaoxing.com";

    /** 获取联系人url */
    private static final String GET_CONTACT_URL = DOMAIN + "/apis/roster/searchRosterUser?puid={uid}&keyword={keyword}&page={page}&pageSize={pageSize}";
    /** 获取部门列表url */
    private static final String GET_DEPARTMENT_URL = DOMAIN + "/apis/dept/getDeptsByServer?fid={fid}&pid={pid}&cpage={page}&pageSize={pageSize}";
    /** 获取部门人员列表url */
    private static final String GET_DEPARTMENT_USER_URL = DOMAIN + "/apis/user/getSubDeptUserInfinite?deptId={deptId}&includeSub={includeSub}&cpage={page}&pagesize={pageSize}";

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    public JSONObject search(Integer uid, String keyword, Pagination pagination){
        String forObject = restTemplate.getForObject(GET_CONTACT_URL, String.class, uid, keyword,pagination.getPage(),pagination.getPageSize());
        JSONObject jsonObject = JSON.parseObject(forObject);
        Assert.isTrue(jsonObject.getInteger("result").equals(1),"微服务人员搜索接口异常:"+jsonObject.getString("errorMsg"));
        return jsonObject.getJSONObject("data");
    }

    public JSONArray getOrganizationDepts(Integer fid, String pid, Pagination pagination) {
        String forObject = restTemplate.getForObject(GET_DEPARTMENT_URL, String.class, fid, pid, pagination.getPage(), pagination.getPageSize());
        JSONObject jsonObject = JSON.parseObject(forObject);
        Assert.isTrue(jsonObject.getInteger("result").equals(1), "微服务部门接口异常:" + jsonObject.getString("errorMsg"));
        return jsonObject.getJSONArray("msg");
    }

    public JSONObject getDeptMembers(Integer deptId, Pagination pagination){
        String forObject = restTemplate.getForObject(GET_DEPARTMENT_USER_URL, String.class, deptId, 0,pagination.getPage(),pagination.getPageSize());
        JSONObject jsonObject = JSON.parseObject(forObject);
        Assert.isTrue(jsonObject.getInteger("result").equals(1),"微服务部门人员接口异常:"+jsonObject.getString("errorMsg"));
        return jsonObject.getJSONObject("data");
    }

}
