package com.chaoxing.activity.dto.manager.wfwform.v2;

import com.chaoxing.activity.util.WfwFormUtils;
import com.chaoxing.activity.util.constant.WfwFormConstant;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.TreeMap;

/**万能表单数据新增参数对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormDataAddParamDTO
 * @description
 * @blame wwb
 * @date 2021-12-13 14:42:53
 */
@Getter
@ToString
public class WfwFormDataAddParamDTO {

    /** 日期格式化 */
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");

    /** 表单id */
    private Integer formId;
    /** 表单所属机构id */
    private Integer fid;
    /** 操作人 */
    private Integer uid;
    /** 控制数据的必填校验是否开启，默认为开启true */
    private Boolean checkRequired;
    /** 表单数据 */
    private String formData;
    /** 时间 */
    private String datetime;
    /** 接口标识 */
    private String sign;
    /** 参数加密串 */
    private String enc;

    private WfwFormDataAddParamDTO() {

    }

    /**构建万能表单数据新增参数对象
     * @Description 
     * @author wwb
     * @Date 2021-12-13 16:02:24
     * @param formId
     * @param formData
     * @param uid
     * @param fid
     * @return com.chaoxing.activity.dto.manager.wfwform.v2.WfwFormDataAddParamDTO
    */
    public static WfwFormDataAddParamDTO build(Integer formId, String formData, Integer uid, Integer fid) {
        return build(formId, formData, uid, fid, WfwFormConstant.SIGN, WfwFormConstant.KEY);
    }

    /**构建万能表单数据新增参数对象
     * @Description 
     * @author wwb
     * @Date 2021-12-13 15:10:34
     * @param formId
     * @param formData
     * @param uid
     * @param fid
     * @param sign
     * @param key
     * @return com.chaoxing.activity.dto.manager.wfwform.v2.WfwFormDataAddParamDTO
    */
    public static WfwFormDataAddParamDTO build(Integer formId, String formData, Integer uid, Integer fid, String sign, String key) {
        WfwFormDataAddParamDTO wfwFormDataAddParam = new WfwFormDataAddParamDTO();
        wfwFormDataAddParam.formId = formId;
        wfwFormDataAddParam.uid = uid;
        wfwFormDataAddParam.fid = fid;
        wfwFormDataAddParam.checkRequired = false;
        LocalDateTime now = LocalDateTime.now();
        wfwFormDataAddParam.datetime = now.format(DATE_TIME_FORMATTER);
        wfwFormDataAddParam.sign = sign;
        // 计算enc
        wfwFormDataAddParam.calEnc(key);
        // 表单数据
        wfwFormDataAddParam.formData = formData;
        return wfwFormDataAddParam;
    }

    /**计算enc
     * @Description 
     * @author wwb
     * @Date 2021-12-13 15:08:37
     * @param key
     * @return void
    */
    private void calEnc(String key) {
        TreeMap<String, Object> paramMap = Maps.newTreeMap();
        paramMap.put("formId", formId);
        paramMap.put("uid", uid);
        paramMap.put("fid", fid);
        paramMap.put("checkRequired", checkRequired);
        paramMap.put("datetime", datetime);
        paramMap.put("sign", sign);
        enc = WfwFormUtils.getEnc(paramMap, key);
    }

}