package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.group.GroupCreateParamDTO;
import com.chaoxing.activity.dto.manager.group.GroupCreateResultDTO;
import com.chaoxing.activity.dto.manager.group.GroupDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**小组服务
 * @author wwb
 * @version ver 1.0
 * @className GroupApiService
 * @description
 * @blame wwb
 * @date 2021-09-17 13:53:18
 */
@Slf4j
@Service
public class GroupApiService {

    private static final String DOMAIN = "https://groupyd.chaoxing.com";
    /** 创建小组yrl */
    private static final String CREATE_URL = DOMAIN + "/apis/circle/addCircle";
    /** 获取小组信息url */
    private static final String GET_URL = DOMAIN + "/apis/circle/getCircle";
    
    /** 小组地址url */
    private static final String GROUP_URL = "http://groupweb.chaoxing.com/pc/topic/topiclist/index?bbsid=%s";

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    /**创建小组
     * @Description 
     * @author wwb
     * @Date 2021-09-17 14:10:11
     * @param groupCreateParamDto
     * @return com.chaoxing.activity.dto.manager.group.GroupCreateResultDTO
    */
    public GroupCreateResultDTO create(GroupCreateParamDTO groupCreateParamDto) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("name", groupCreateParamDto.getName());
        params.add("puid", groupCreateParamDto.getPuid());
        String result = restTemplate.postForObject(CREATE_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Objects.equals(jsonObject.getInteger("result"), 1)) {
            return JSON.parseObject(jsonObject.getString("data"), GroupCreateResultDTO.class);
        } else {
            String message = jsonObject.getString("errorMsg");
            log.error("根据参数:{}创建小组error:{}", JSON.toJSONString(groupCreateParamDto), message);
            throw new BusinessException(message);
        }
    }

    /**获取小组信息
     * @Description 
     * @author wwb
     * @Date 2021-09-17 14:27:06
     * @param id
     * @param createrPuid
     * @return com.chaoxing.activity.dto.manager.group.GroupDTO
    */
    public GroupDTO getGroup(Integer id, Integer createrPuid) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("circleId", id);
        params.add("puid", createrPuid);
        String result = restTemplate.postForObject(GET_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Objects.equals(jsonObject.getInteger("result"), 1)) {
            return JSON.parseObject(jsonObject.getString("data"), GroupDTO.class);
        } else {
            String message = jsonObject.getString("errorMsg");
            log.error("根据小组id:{}获取小组error:{}", id, message);
            throw new BusinessException(message);
        }
    }

    /**小组的url
     * @Description 
     * @author wwb
     * @Date 2021-09-17 15:55:32
     * @param bbsid
     * @return java.lang.String
    */
    public String getGroupUrl(String bbsid) {
        return String.format(GROUP_URL, bbsid);
    }

}