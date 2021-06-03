package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.WfwGroupDTO;
import com.google.common.collect.Lists;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<WfwGroupDTO> listGroupByFid(Integer fid){
        List<WfwGroupDTO> wfwGroupResult = Lists.newArrayList();
        String enc = DigestUtils.md5Hex(fid + ENC_KEY + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String url = String.format(GET_ALL_GROUP_URL, fid, enc);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        String rootId = jsonObject.getString("gid");
        JSONObject dataMap = jsonObject.getJSONObject("map");
        List<WfwGroupDTO> allWfwGroups = new ArrayList<>();
        for (String s : dataMap.keySet()) {
            allWfwGroups.addAll(JSONArray.parseArray(dataMap.getString(s), WfwGroupDTO.class));
        }
        if (CollectionUtils.isNotEmpty(allWfwGroups)) {
            Map<String, List<WfwGroupDTO>> gidGroups = allWfwGroups.stream().collect(Collectors.groupingBy(WfwGroupDTO::getGid));
            wfwGroupResult = listSub(gidGroups, rootId, 1);
        }
        return wfwGroupResult;
    }

    private List<WfwGroupDTO> listSub(Map<String, List<WfwGroupDTO>> gidGroups, String gid, Integer level) {
        List<WfwGroupDTO> result = gidGroups.get(gid);
        if (CollectionUtils.isNotEmpty(result)) {
            List<WfwGroupDTO> children = Lists.newArrayList();
            for (WfwGroupDTO wfwGroupDTO : result) {
                wfwGroupDTO.setGroupLevel(level);
                String id = wfwGroupDTO.getId();
                children.addAll(listSub(gidGroups, id, level + 1));
            }
            result.addAll(children);
        } else {
            result = Lists.newArrayList();
        }
        return result;
    }

    /**获取有层级的组织架构
     * @Description 
     * @author wwb
     * @Date 2021-06-02 15:32:34
     * @param fid
     * @return java.util.List<com.chaoxing.activity.dto.manager.WfwGroupDTO>
    */
    public List<WfwGroupDTO> listHierarchyGroupByFid(Integer fid){
        List<WfwGroupDTO> wfwGroupResult = Lists.newArrayList();
        String enc = DigestUtils.md5Hex(fid + ENC_KEY + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        String url = String.format(GET_ALL_GROUP_URL, fid, enc);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        String rootId = jsonObject.getString("gid");
        JSONObject dataMap = jsonObject.getJSONObject("map");
        List<WfwGroupDTO> allWfwGroups = new ArrayList<>();
        for (String s : dataMap.keySet()) {
            allWfwGroups.addAll(JSONArray.parseArray(dataMap.getString(s), WfwGroupDTO.class));
        }
        if (CollectionUtils.isNotEmpty(allWfwGroups)) {
            Map<String, List<WfwGroupDTO>> gidGroups = allWfwGroups.stream().collect(Collectors.groupingBy(WfwGroupDTO::getGid));
            wfwGroupResult = listHierarchySub(gidGroups, rootId, 1);
        }
        return wfwGroupResult;
    }

    private List<WfwGroupDTO> listHierarchySub(Map<String, List<WfwGroupDTO>> gidGroups, String gid, Integer level) {
        List<WfwGroupDTO> result = gidGroups.get(gid);
        if (CollectionUtils.isNotEmpty(result)) {
            for (WfwGroupDTO wfwGroupDTO : result) {
                wfwGroupDTO.setGroupLevel(level);
                String id = wfwGroupDTO.getId();
                List<WfwGroupDTO> children = listHierarchySub(gidGroups, id, level + 1);
                if (CollectionUtils.isEmpty(children)) {
                    children = null;
                }
                wfwGroupDTO.setChildren(children);
            }
        } else {
            result = Lists.newArrayList();
        }
        return result;
    }
}
