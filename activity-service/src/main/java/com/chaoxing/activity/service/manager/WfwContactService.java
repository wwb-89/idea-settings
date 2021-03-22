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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class WfwContactService {

    private static final String SEARCH_URL = "https://contactsyd.chaoxing.com/apis/roster/searchRosterUser?puid={uid}&keyword={keyword}&page={page}&pageSize={pageSize}";

    private static final String DEPTS_URL = "https://contactsyd.chaoxing.com/apis/dept/getDeptsByServer?fid={fid}&pid={pid}&cpage={page}&pageSize={pageSize}";

    private static final String DEPT_MEMBERS_URL = "https://contactsyd.chaoxing.com/apis/user/getSubDeptUserInfinite?deptId={deptId}&includeSub={includeSub}&cpage={page}&pagesize={pageSize}";

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    public JSONObject search(Integer uid, String keyword, Pagination pagination){
        String forObject = restTemplate.getForObject(SEARCH_URL, String.class, uid, keyword,pagination.getPage(),pagination.getPageSize());
        JSONObject jsonObject = JSON.parseObject(forObject);
        Assert.isTrue(jsonObject.getInteger("result").equals(1),"微服务人员搜索接口异常:"+jsonObject.getString("errorMsg"));
        return jsonObject.getJSONObject("data");
    }

    public JSONArray getOrganizationDepts(Integer fid, String pid, Pagination pagination) {
        String forObject = restTemplate.getForObject(DEPTS_URL, String.class, fid, pid, pagination.getPage(), pagination.getPageSize());
        JSONObject jsonObject = JSON.parseObject(forObject);
        Assert.isTrue(jsonObject.getInteger("result").equals(1), "微服务部门接口异常:" + jsonObject.getString("errorMsg"));
        return jsonObject.getJSONArray("msg");
    }

    public JSONObject getDeptMembers(Integer deptId, Pagination pagination){
        String forObject = restTemplate.getForObject(DEPT_MEMBERS_URL, String.class, deptId, 0,pagination.getPage(),pagination.getPageSize());
        JSONObject jsonObject = JSON.parseObject(forObject);
        Assert.isTrue(jsonObject.getInteger("result").equals(1),"微服务部门人员接口异常:"+jsonObject.getString("errorMsg"));
        return jsonObject.getJSONObject("data");
    }

    public JSONArray getByFanyaMultiple(Integer uid) {
        String forObject = restTemplate.getForObject("http://mooc1-api.chaoxing.com/gas/person?userid={uid}&fields=id,userid,loginname,username,phone,group1,roleids,schoolid,createtime,status&selectuser=true", String.class, uid);
        JSONObject fanyaUserJSONObject = JSON.parseObject(forObject);
        JSONArray fanyaUserArray = fanyaUserJSONObject.getJSONArray("data");
        for (int i = 0; i < fanyaUserArray.size(); i++) {
            JSONObject fanyaUser = fanyaUserArray.getJSONObject(i);
            Integer fid = fanyaUser.getIntValue("schoolid");
            fanyaUser.put("fname", getFname(fid));
            fanyaUser.put("fid", fid);
        }
        return fanyaUserArray;
    }

    public String getFname(Integer fid) {
        if (fid == 0) {
            return "超星网";
        }
        String forObject = restTemplate.getForObject("http://passport.basicedu.chaoxing.com/passport/organization/{fid}/info", String.class, fid);
        JSONObject jsonObject = JSON.parseObject(forObject);
        String fname = jsonObject.getString("name");
        return fname;
    }


}
