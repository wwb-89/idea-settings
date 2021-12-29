package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormAdvanceSearchFilterConditionDTO;
import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**微服务表单审批api服务
 * @author wwb
 * @version ver 1.0
 * @className WfwApprovalApiService
 * @description
 * @blame wwb
 * @date 2021-05-10 17:46:00
 */
@Slf4j
@Service
public class WfwApprovalApiService {

    /** 日期格式化 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
    /** sign */
    private static final String SIGN = "approveData_activity";
    /** key */
    private static final String KEY = "XtTpP2MjfoHZa^5!s8";

    /** 获取表单指定数据url */
    private static final String LIST_FORM_SPECIFIED_DATA_URL = DomainConstant.WFW_FORM_API + "/api/approve/forms/user/data/list";
    /** 获取表单数据列表 */
    private static final String ADVANCED_SEARCH_URL = DomainConstant.WFW_FORM_API + "/api/approve/forms/advanced/search/list";
    /** 表单每页数据限制 */
    private static final int MAX_PAGE_SIZE_LIMIT = 100;

    @Resource(name = "restTemplateProxy")
    private RestTemplate restTemplate;

    private String getEnc(Map<String, Object> encParamMap) {
        StringBuilder enc = new StringBuilder();
        for (Map.Entry<String, Object> entry : encParamMap.entrySet()) {
            enc.append("[").append(entry.getKey()).append("=")
                    .append(entry.getValue()).append("]");
        }
        return DigestUtils.md5Hex(enc + "[" + KEY + "]");
    }

    /**获取表单记录
     * @Description
     * @author wwb
     * @Date 2021-08-30 11:35:11
     * @param formUserId
     * @param formId
     * @param fid
     * @return com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO
     */
    public FormDataDTO getFormRecord(@NotNull Integer formUserId, Integer formId, Integer fid) {
        List<Integer> formUserIds = Lists.newArrayList();
        formUserIds.add(formUserId);
        List<FormDataDTO> formDataDtos = listFormRecord(formUserIds, formId, fid);
        return Optional.ofNullable(formDataDtos).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
    }

    /**查询表单记录
     * @Description
     * @author wwb
     * @Date 2021-08-30 11:04:08
     * @param formUserIds
     * @param formId
     * @param fid
     * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
     */
    private List<FormDataDTO> listFormRecord(List<Integer> formUserIds, Integer formId, Integer fid) {
        if (CollectionUtils.isEmpty(formUserIds)) {
            return Lists.newArrayList();
        }
        TreeMap<String, Object> paramsMap = Maps.newTreeMap();
        paramsMap.put("deptId", fid);
        paramsMap.put("formId", formId);
        paramsMap.put("formUserIds", String.join(",", Optional.of(formUserIds).orElse(Lists.newArrayList()).stream().map(String::valueOf).collect(Collectors.toList())));
        paramsMap.put("sign", SIGN);
        paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        paramsMap.put("enc", getEnc(paramsMap));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(paramsMap);
        String result = restTemplate.postForObject(LIST_FORM_SPECIFIED_DATA_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getBoolean("success")) {
            JSONArray jsonArray = jsonObject.getJSONObject("data").getJSONArray("formUserList");
            if (jsonArray != null) {
                return JSON.parseArray(jsonArray.toJSONString(), FormDataDTO.class);
            } else {
                return Lists.newArrayList();
            }
        } else {
            String errorMessage = jsonObject.getString("msg");
            throw new BusinessException(errorMessage);
        }
    }

    /**查询表单下的所有数据
     * @Description
     * @author wwb
     * @Date 2021-08-30 16:08:14
     * @param formId
     * @param fid
     * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
     */
    public List<FormDataDTO> listFormRecord(Integer formId, Integer fid) {
        FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto = FormAdvanceSearchFilterConditionDTO.builder()
                .model(FormAdvanceSearchFilterConditionDTO.ModelEnum.AND.getValue())
                .filters(Lists.newArrayList())
                .build();
        return advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid, 1, MAX_PAGE_SIZE_LIMIT);
    }


    /**高级检索表单数据
     * @Description
     * @author wwb
     * @Date 2021-08-30 22:43:47
     * @param formAdvanceSearchFilterConditionDto
     * @param formId
     * @param fid
     * @param pageNum
     * @param pageSize
     * @return java.util.List<com.chaoxing.secondclassroom.dto.manager.form.FormDataDTO>
     */
    public List<FormDataDTO> advancedSearchAll(FormAdvanceSearchFilterConditionDTO formAdvanceSearchFilterConditionDto, Integer formId, Integer fid, Integer pageNum, Integer pageSize) {
        TreeMap<String, Object> paramsMap = Maps.newTreeMap();
        paramsMap.put("deptId", fid);
        paramsMap.put("formId", formId);
        paramsMap.put("cpage", pageNum);
        paramsMap.put("pageSize", pageSize);
        paramsMap.put("datetime", LocalDateTime.now().format(DATE_TIME_FORMATTER));
        paramsMap.put("sign", SIGN);
        paramsMap.put("enc", getEnc(paramsMap));
        paramsMap.put("searchStr", JSON.toJSONString(formAdvanceSearchFilterConditionDto));
        MultiValueMap<String, Object> params = new LinkedMultiValueMap<>();
        params.setAll(paramsMap);
        String result = restTemplate.postForObject(ADVANCED_SEARCH_URL, params, String.class);
        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject.getBoolean("success")) {
            List<FormDataDTO> formDataDtos = Lists.newArrayList();
            JSONObject data = jsonObject.getJSONObject("data");
            JSONArray jsonArray = data.getJSONArray("dataList");
            formDataDtos.addAll(FormDataDTO.buildFromAdvanceSearchResult(jsonArray));
            Integer totalPage = data.getInteger("totalPage");
            if (totalPage > pageNum) {
                formDataDtos.addAll(advancedSearchAll(formAdvanceSearchFilterConditionDto, formId, fid, ++pageNum, pageSize));
            }
            return formDataDtos;
        } else {
            String errorMessage = jsonObject.getString("msg");
            throw new BusinessException(errorMessage);
        }
    }

}