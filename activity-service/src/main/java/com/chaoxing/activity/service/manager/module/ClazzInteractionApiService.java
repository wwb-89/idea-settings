package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.ClazzInteractionDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2021/12/2 5:30 下午
 * @version: 1.0
 */
@Slf4j
@Service
public class ClazzInteractionApiService {

    /** 活动班级互动创建推送地址 */
    private static final String COURSE_PUSH_URL = DomainConstant.XIAMEN_TRAINING_PLATFORM_API + "/course/push";

    @Resource
    private RestTemplate restTemplate;

    /**活动课程创建
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-02 17:37:29
     * @param activityId
     * @param activityName
     * @param uid
     * @param coverUrl
     * @param formId
     * @param fid
     * @param flag
     * @return
     */
    public ClazzInteractionDTO clazzCourseCreate(Integer activityId, String activityName, Integer uid, String coverUrl, Integer formId, Integer fid, String flag) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> params = new LinkedMultiValueMap();
        params.add("uid", uid);
        params.add("activityId", activityId);
        params.add("activityName", activityName);
        params.add("coverUrl", coverUrl);
        params.add("formId", formId);
        params.add("fid", fid);
        params.add("flag", flag);
        String result = restTemplate.postForObject(COURSE_PUSH_URL, new HttpEntity(params, httpHeaders), String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        String message = jsonObject.getString("message");
        Integer code = jsonObject.getInteger("code");
        if (Objects.equals(code, 1)) {
            return JSON.parseObject(jsonObject.getJSONObject("data").toJSONString(), ClazzInteractionDTO.class);
        } else {
            log.error("创建班级互动error:{}", message);
            throw new BusinessException(message);
        }
    }


}
