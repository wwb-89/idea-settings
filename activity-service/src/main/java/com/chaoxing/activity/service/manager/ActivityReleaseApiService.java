package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.activity.create.ActivityCreateFromActivityReleaseParamDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Objects;

/**活动发布平台api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleaseApiService
 * @description
 * @blame wwb
 * @date 2021-09-15 15:52:23
 */
@Slf4j
@Service
public class ActivityReleaseApiService {

    private static final String GET_ACTIVITY_CREATE_URL = DomainConstant.ACTIVITY + "/api/activity/%d/to-activity-engine-param";

    @Resource
    private RestTemplate restTemplate;

    /**根据活动发布平台的活动id查询活动引擎需要的活动创建对象
     * @Description 
     * @author wwb
     * @Date 2021-09-15 16:09:31
     * @param activityId
     * @return com.chaoxing.activity.dto.activity.create.ActivityCreateFromActivityReleaseParamDTO
    */
    public ActivityCreateFromActivityReleaseParamDTO getActivityCreateFromActivityReleaseParam(Integer activityId) {
        String url = String.format(GET_ACTIVITY_CREATE_URL, activityId);
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (Objects.equals(jsonObject.getBoolean("success"), true)) {
            return JSON.parseObject(jsonObject.getString("data"), ActivityCreateFromActivityReleaseParamDTO.class);
        } else {
            String message = jsonObject.getString("message");
            log.error("根据url:{}获取活动发布平台的活动error:{}", url, message);
            throw new BusinessException(message);
        }
    }

}