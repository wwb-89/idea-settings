package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.util.CookieUtils;
import com.chaoxing.activity.util.URLUtils;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
     * @param request
     * @param url
     * @return java.util.List<java.lang.Object>
     */
    public List<?> getDataFromThirdPartyUrl(HttpServletRequest request, String url, List<String> cookieKeys, Class<?> clazz) {
        Map<String, String> paramMap = CookieUtils.getCookieMap(request, cookieKeys);
        String realUrl = URLUtils.packageParam2URL(url, paramMap);

        String result = restTemplate.getForObject(realUrl, String.class);
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
