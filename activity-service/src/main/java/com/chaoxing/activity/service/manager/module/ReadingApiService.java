package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.module.ReadingModuleDataDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/2 15:49
 * <p>
 */
@Slf4j
@Service
public class ReadingApiService {

    /** 阅读创建更新地址 */
    private static final String CREATE_URL = "http://xueya.chaoxing.com/xy-ucr/school-reading-module/add-or-update-module";

    @Resource
    private RestTemplate restTemplate;

    /**创建阅读
    * @Description
    * @author huxiaolong
    * @Date 2021-09-02 16:32:24
    * @param request
    * @param activityName
    * @return com.chaoxing.activity.dto.module.ReadingModuleDataDTO
    */
    public ReadingModuleDataDTO create(HttpServletRequest request, String activityName) {
        List<String> cookies = Lists.newArrayList();
        for (Cookie cookie : request.getCookies()) {
            cookies.add(cookie.getName() + "=" + cookie.getValue());
        }
        return create(cookies, activityName);
    }

    public ReadingModuleDataDTO create(List<String> cookies, String activityName) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.put(HttpHeaders.COOKIE, cookies);
        JSONObject obj = new JSONObject();
        obj.put("name", activityName);
        HttpEntity<JSONObject> httpEntity = new HttpEntity<>(obj, httpHeaders);
        String result = restTemplate.postForObject(CREATE_URL, httpEntity, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        String message = jsonObject.getString("message");
        if (Objects.equals(message, "success")) {
            return JSON.parseObject(jsonObject.getJSONObject("data").getJSONObject("module").toJSONString(), ReadingModuleDataDTO.class);
        } else {
            log.error("创建阅读error:{}", message);
            throw new BusinessException(message);
        }
    }



}
