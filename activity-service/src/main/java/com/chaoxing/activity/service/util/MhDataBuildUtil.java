package com.chaoxing.activity.service.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.model.Classify;
import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * @description:
 * @author: huxiaolong
 * @date: 2022/1/6 2:13 下午
 * @version: 1.0
 */
public class MhDataBuildUtil {


    /**封装门户分类数据
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:07:23
     * @param classifies
     * @return 
     */
    public static JSONObject buildClassifies(List<Classify> classifies) {
        JSONObject firstLevel = new JSONObject();
        firstLevel.put("id", "");
        firstLevel.put("name", "活动类型");
        firstLevel.put("description", "");
        firstLevel.put("count", 0);
        JSONArray subs = new JSONArray();
        if (CollectionUtils.isNotEmpty(classifies)) {
            firstLevel.put("count", classifies.size());
            for (Classify classify : classifies) {
                JSONObject item = new JSONObject();
                item.put("id", classify.getId());
                item.put("name", classify.getName());
                item.put("description", "");
                subs.add(item);
            }
        }
        firstLevel.put("subs", subs);
        return firstLevel;
    }

    /**
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:07:38
     * @param key
     * @param value
     * @param flag
     * @return 
     */
    public static JSONObject buildField(String key, Object value, Integer flag) {
        JSONObject field = new JSONObject();
        field.put("key", key);
        field.put("value", value);
        field.put("flag", flag);
        return field;
    }
    
    /**封装门户接收单个数据体
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:08:23
     * @param iconUrl
     * @param key
     * @param value
     * @return 
     */
    private static MhGeneralAppResultDataDTO buildField(String iconUrl,
                                                        String key,
                                                        String value) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        int flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("图标")
                .value(iconUrl)
                .type("3")
                .flag(String.valueOf(flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("标题")
                .value(key)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("内容")
                .value(value)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        item.setFields(fields);
        return item;
    }

    /**封装门户接收单个数据体
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:08:51
     * @param iconUrl
     * @param key
     * @param value
     * @param mainFields
     * @return 
     */
    public static void buildField(String iconUrl, String key, String value, List<MhGeneralAppResultDataDTO> mainFields) {
        mainFields.add(buildField(iconUrl, key, value));
    }

    /**封装门户接收数据体（带单位）
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:09:24
     * @param iconUrl
     * @param key
     * @param value
     * @param unit
     * @param mainFields
     * @return 
     */
    public static void buildFieldWithUnit(String iconUrl,
                            String key,
                            String value,
                            String unit,
                            List<MhGeneralAppResultDataDTO> mainFields) {
        MhGeneralAppResultDataDTO item = buildField(iconUrl, key, value);
        if (CollectionUtils.isEmpty(item.getFields())) {
            item.setFields(Lists.newArrayList());
        }
        int flag = item.getFields().size();
        item.getFields().add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("单位")
                .value(unit)
                .type("3")
                .flag(String.valueOf(++flag))
                .build());
        mainFields.add(item);
    }

    /**封装门户接收数据体（带osrUrl）
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:10:25
     * @param iconUrl
     * @param key
     * @param value
     * @param osrUrl
     * @param mainFields
     * @return 
     */
    public static void buildFieldWithOsrUrl(String iconUrl, String key, String value, String osrUrl, List<MhGeneralAppResultDataDTO> mainFields) {
        MhGeneralAppResultDataDTO item = buildField(iconUrl, key, value);
        if (StringUtils.isNotBlank(osrUrl)) {
            item.setOrsUrl(osrUrl);
        }
        mainFields.add(item);
    }
    
    /**封装门户按钮数据体
     * @Description 
     * @author huxiaolong
     * @Date 2022-01-10 11:11:16
     * @param key
     * @param iconUrl
     * @param url
     * @param type
     * @param isAjax
     * @param sequence
     * @return
     */
    public static MhGeneralAppResultDataDTO buildBtnField(String key, String iconUrl, String url, String type, boolean isAjax, Integer sequence) {
        MhGeneralAppResultDataDTO item = MhGeneralAppResultDataDTO.buildDefault();
        List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> fields = Lists.newArrayList();
        if (isAjax) {
            item.setType(7);
        }
        item.setOrsUrl(url);
        int flag = 0;
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("封面")
                .value(iconUrl)
                .type("3")
                .flag(String.valueOf(flag))
                .build());
        fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                .key("标题")
                .orsUrl(isAjax ? url : "")
                .value(key)
                .type(isAjax ? "7" : "3")
                .flag(String.valueOf(++flag))
                .build());
        if (StringUtils.isNotBlank(type)) {
            fields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
                    .key("按钮类型")
                    .value(type)
                    .type(isAjax ? "7" : "3")
                    .flag(String.valueOf(++flag))
                    .build());
        }
        item.setSequence(sequence);
        item.setFields(fields);
        return item;
    }





}
