package com.chaoxing.activity.service.manager.module;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.CertificateFieldDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**证书api服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateApiService
 * @description
 * @blame wwb
 * @date 2021-12-15 10:41:47
 */
@Slf4j
@Service
public class CertificateApiService {

    /** 系统模版id */
    private static final Integer SYSTEM_TEMPLATE_ID = 12723;
    /** 复制模版url */
    private static final String COPY_TEMPLATE_URL = DomainConstant.CERTIFICATE + "/template/copy";
    /** 模版配置页面地址 */
    private static final String TEMPLATE_CONFIG_URL = DomainConstant.CERTIFICATE + "/template/newview?tid=%d&uid=%d&fid=%d&enc=";
    /** 下载模版url */
    private static final String DOWNLOAD_TEMPLATE_URL = DomainConstant.CERTIFICATE + "/template/topdf?tid=%d&uid=%d&fid=%d&params=%s&enc";

    @Resource
    private RestTemplate restTemplate;

    /**结果处理
     * @Description
     * @author wwb
     * @Date 2021-12-15 10:51:21
     * @param jsonObject
     * @param successCallback
     * @param errorCallback
     * @return T
    */
    private <T> T resultHandle(JSONObject jsonObject, Supplier<T> successCallback, Consumer<String> errorCallback) {
        Boolean success = jsonObject.getBoolean("success");
        success = Optional.ofNullable(success).orElse(Boolean.FALSE);
        if (success) {
            return successCallback.get();
        } else {
            errorCallback.accept(jsonObject.getString("msg"));
            return null;
        }
    }

    /**复制模版
     * @Description 
     * @author wwb
     * @Date 2021-12-15 10:46:31
     * @param uid
     * @param fid
     * @return java.lang.Integer
    */
    public Integer copyTemplate(Integer uid, Integer fid) {
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.add("uid", uid);
        params.add("fid", fid);
        params.add("tid", SYSTEM_TEMPLATE_ID);
        params.add("enc", "");
        String result = restTemplate.postForObject(COPY_TEMPLATE_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        return resultHandle(jsonObject, () -> jsonObject.getInteger("tid"), (message) -> {
            throw new BusinessException("复制证书模版失败");
        });
    }

    /**生成模版配置页面url
     * @Description 
     * @author wwb
     * @Date 2021-12-15 10:57:28
     * @param tid
     * @param uid
     * @param fid
     * @return java.lang.String
    */
    public String generateTemplateConfigUrl(Integer tid, Integer uid, Integer fid) {
        return String.format(TEMPLATE_CONFIG_URL, tid, uid, fid);
    }

    /**下载证书
     * @Description 
     * @author wwb
     * @Date 2021-12-15 11:27:50
     * @param tid
     * @param uid
     * @param fid
     * @param certificateField
     * @return java.lang.String
    */
    public String getDownloadUrl(Integer tid, Integer uid, Integer fid, CertificateFieldDTO certificateField) throws UnsupportedEncodingException {
        String params = URLEncoder.encode(JSON.toJSONString(certificateField), StandardCharsets.UTF_8.name());
        return String.format(DOWNLOAD_TEMPLATE_URL, tid, uid, fid, params);
    }

}