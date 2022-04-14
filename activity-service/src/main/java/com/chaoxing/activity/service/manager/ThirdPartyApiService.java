package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/3 16:36
 * <p>
 */
@Slf4j
@Service
public class ThirdPartyApiService {

    @Resource
    private RestTemplate restTemplate;

    /**从第三方url获取数据
     * @Description
     * @author huxiaolong
     * @Date 2021-09-03 14:18:11
     * @param url
     * @param clazz
     * @return java.util.List<java.lang.Object>
     */
    public <T> List<T> getDataFromThirdPartyUrl(String url, Class<T> clazz) {
        String result = restTemplate.getForObject(url, String.class);
        JSONObject jsonObject = JSON.parseObject(result);

        if (jsonObject.getBoolean("success")) {
            return JSON.parseArray(jsonObject.getJSONArray("data").toJSONString(), clazz);
        } else {
            String errorMessage = jsonObject.getString("message");
            log.error("查询数据列表失败:{}", errorMessage);
            throw new BusinessException(errorMessage);
        }
    }

}
