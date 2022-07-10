package com.chaoxing.activity.service.manager.wfw.v2;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.wfwform.v2.WfwFormDataAddParamDTO;
import com.chaoxing.activity.dto.manager.wfwform.v2.WfwFormDataUpdateParamDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.Optional;
import java.util.TreeMap;

/**新的万能表单接口服务
 * @author wwb
 * @version ver 1.0
 * @className WfwFormNewApiService
 * @description
 * @blame wwb
 * @date 2021-12-13 14:38:35
 */
@Slf4j
@Service
public class WfwFormNewApiService {

    /** 数据新增接口url */
    private static final String DATA_ADD_URL = DomainConstant.WFW_FORM_API + "/api/apps/forms/data/add";
    /** 数据更新接口url */
    private static final String DATA_UPDATE_URL = DomainConstant.WFW_FORM_API + "/api/apps/forms/data/update";

    @Resource
    private RestTemplate restTemplate;

    /**数据新增
     * @Description 
     * @author wwb
     * @Date 2021-12-13 16:10:18
     * @param wfwFormDataAddParam
     * @return java.lang.Integer
    */
    public Integer dataAdd(WfwFormDataAddParamDTO wfwFormDataAddParam) {
        String formData = wfwFormDataAddParam.getFormData();
        if (StringUtils.isBlank(formData)) {
            throw new BusinessException("表单数据不能为空");
        }
        TreeMap<String, Object> paramMap = Maps.newTreeMap();
        paramMap.put("formId", wfwFormDataAddParam.getFormId());
        paramMap.put("uid", wfwFormDataAddParam.getUid());
        paramMap.put("fid", wfwFormDataAddParam.getFid());
        paramMap.put("checkRequired", wfwFormDataAddParam.getCheckRequired());
        paramMap.put("formData", wfwFormDataAddParam.getFormData());
        paramMap.put("datetime", wfwFormDataAddParam.getDatetime());
        paramMap.put("sign", wfwFormDataAddParam.getSign());
        paramMap.put("enc", wfwFormDataAddParam.getEnc());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap();
        params.setAll(paramMap);
        String result = restTemplate.postForObject(DATA_ADD_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Boolean success = jsonObject.getBoolean("success");
        success = Optional.ofNullable(success).orElse(Boolean.FALSE);
        if (success) {
            jsonObject = jsonObject.getJSONObject("data");
            return jsonObject.getInteger("formUserId");
        } else {
            String errorMessage = jsonObject.getString("msg");
            log.error("根据参数:{}, 填写表单error:{}", JSON.toJSONString(paramMap), errorMessage);
            throw new BusinessException(errorMessage);
        }
    }

    /**修改数据
     * @Description 
     * @author wwb
     * @Date 2021-12-13 16:24:53
     * @param wfwFormDataUpdateParam
     * @return void
    */
    public void dataUpdate(WfwFormDataUpdateParamDTO wfwFormDataUpdateParam) {
        String formData = wfwFormDataUpdateParam.getFormData();
        if (StringUtils.isBlank(formData)) {
            throw new BusinessException("表单数据不能为空");
        }
        TreeMap<String, Object> paramMap = Maps.newTreeMap();
        paramMap.put("formUserId", wfwFormDataUpdateParam.getFormUserId());
        paramMap.put("fid", wfwFormDataUpdateParam.getFid());
        paramMap.put("checkRequired", wfwFormDataUpdateParam.getCheckRequired());
        paramMap.put("formData", wfwFormDataUpdateParam.getFormData());
        paramMap.put("datetime", wfwFormDataUpdateParam.getDatetime());
        paramMap.put("sign", wfwFormDataUpdateParam.getSign());
        paramMap.put("enc", wfwFormDataUpdateParam.getEnc());
        MultiValueMap<String, Object> params = new LinkedMultiValueMap();
        params.setAll(paramMap);
        String result = restTemplate.postForObject(DATA_UPDATE_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        Boolean success = jsonObject.getBoolean("success");
        success = Optional.ofNullable(success).orElse(Boolean.FALSE);
        if (!success) {
            String errorMessage = jsonObject.getString("msg");
            log.error("根据参数:{}, 填写表单error:{}", JSON.toJSONString(paramMap), errorMessage);
            throw new BusinessException(errorMessage);
        }
    }

}