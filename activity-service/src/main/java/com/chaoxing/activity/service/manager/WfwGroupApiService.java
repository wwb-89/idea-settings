package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author xhl
 * @version ver 1.0
 * @className WfwGroupApiService
 * @description
 * @blame xhl
 * @date 2021-03-10 15:00:30
 */
@Service
public class WfwGroupApiService {
    /** 根据fid和父级id获取组织架构 */
    public static final String GET_GROUP_URL = "http://uc1-ans.chaoxing.com/gas/usergroup?fid=%d&gid=%s&fields=id,groupname,gid&offset=0&limit=1000";

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    /**
     * 根据fid和gid 获取机构组织架构
     * @param fid
     * @param gid
     * @return
     */
    public List<WfwGroupDTO> getGroupByGid(Integer fid, String gid){
        String url = String.format(GET_GROUP_URL, fid, gid);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        String arrStr = jsonObject.getString("data");
        List<WfwGroupDTO> wfwGroups = JSONArray.parseArray(arrStr, WfwGroupDTO.class);
        return wfwGroups;
    }

}
