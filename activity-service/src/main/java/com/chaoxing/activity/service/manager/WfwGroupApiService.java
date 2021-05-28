package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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
    public static final String GET_GROUP_URL = "http://uc1-ans.chaoxing.com/gas/usergroup?fid=%d&gid=%d&fields=id,groupname,gid,soncount&offset=0&limit=1000";


    public static final String GET_ALL_GROUP_URL = "http://uc1-ans.chaoxing.com/apis/getallusergroup?fid=%d&enc=%s";


    /** key */
    private static final String ENC_KEY = "mic^ruso&ke@y";

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    /**获取机构的架构组别
     * @Description 
     * @author wwb
     * @Date 2021-03-29 15:01:36
     * @param fid
     * @param gid
     * @return java.util.List<com.chaoxing.activity.dto.manager.WfwGroupDTO>
    */
    public List<WfwGroupDTO> getGroupByGid(Integer fid, Integer gid){
        String url = String.format(GET_GROUP_URL, fid, gid);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        String arrStr = jsonObject.getString("data");
        List<WfwGroupDTO> wfwGroups = JSONArray.parseArray(arrStr, WfwGroupDTO.class);
        return wfwGroups;
    }

    /**获取机构的架构组别
     * @Description
     * @author wwb
     * @Date 2021-03-29 15:01:36
     * @param fid
     * @return java.util.List<com.chaoxing.activity.dto.manager.WfwGroupDTO>
    */
    public List<WfwGroupDTO> getGroupByFid(Integer fid){
        String enc = DigestUtils.md5Hex(fid + ENC_KEY + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String url = String.format(GET_ALL_GROUP_URL, fid, enc);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        JSONObject dataMap = jsonObject.getJSONObject("map");
        List<WfwGroupDTO> wfwGroups = new ArrayList<>();
        for (String s : dataMap.keySet()) {
            wfwGroups.addAll(JSONArray.parseArray(dataMap.getString(s), WfwGroupDTO.class));
        }
//        List<WfwGroupDTO> wfwGroups = JSONArray.parseArray(arrStr, WfwGroupDTO.class);
        return wfwGroups;
    }

}
