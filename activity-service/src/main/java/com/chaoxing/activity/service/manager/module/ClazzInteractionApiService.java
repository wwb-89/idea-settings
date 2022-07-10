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

/**班级互动api服务
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
    /** 用户加入互动班级 */
    private static final String CLASS_ADD_USER_URL = DomainConstant.XIAMEN_TRAINING_PLATFORM_API + "/api/join-class?activityId=%d&uid=%d";
    /** 用户移除互动班级 */
    private static final String CLASS_REMOVE_USER_URL = DomainConstant.XIAMEN_TRAINING_PLATFORM_API + "/api/exit-class?activityId=%d&uid=%d";

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
     * @param startTime
     * @param endTime
     * @return
     */
    public ClazzInteractionDTO clazzCourseCreate(Integer activityId, String activityName, Integer uid, String coverUrl,
                                                 Integer formId, Integer fid, String flag, String startTime, String endTime) {
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
        params.add("startTime", startTime);
        params.add("endTime", endTime);
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

    /**班级添加用户
     * @Description 
     * @author wwb
     * @Date 2021-12-29 18:16:06
     * @param uid
     * @param activityId
     * @return void
    */
    public void classAddUser(Integer uid, Integer activityId) {
        String url = String.format(CLASS_ADD_USER_URL, activityId, uid);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Integer code = jsonObject.getInteger("code");
        if (!Objects.equals(code, 1)) {
            String message = jsonObject.getString("message");
            log.error("根据uid:{}, 活动id:{} 将用户加入班级error:{}", uid, activityId, message);
            throw new BusinessException(message);
        }
    }

    /**班级移除用户
     * @Description 
     * @author wwb
     * @Date 2021-12-29 18:17:21
     * @param uid
     * @param activityId
     * @return void
    */
    public void classRemoveUser(Integer uid, Integer activityId) {
        String url = String.format(CLASS_REMOVE_USER_URL, activityId, uid);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Integer code = jsonObject.getInteger("code");
        if (!Objects.equals(code, 1)) {
            String message = jsonObject.getString("message");
            log.error("根据uid:{}, 活动id:{} 将用户移除班级error:{}", uid, activityId, message);
            throw new BusinessException(message);
        }
    }

}
