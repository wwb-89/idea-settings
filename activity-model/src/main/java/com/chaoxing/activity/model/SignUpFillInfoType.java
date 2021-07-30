package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 报名填报信息类型表：默认、双选会、微服务表单
 * @className: SignUpFillInfoType, table_name: t_sign_up_fill_info_type
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:13
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sign_up_fill_info_type")
public class SignUpFillInfoType {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 模版组件关联id; column: template_component_id*/
    private Integer templateComponentId;
    /** 类型; column: type*/
    private String type;
    /** 模板类型; column: template_type*/
    private String templateType;

    public SignUpFillInfoType cloneToNewTemplateComponentId(Integer templateComponentId) {
        return SignUpFillInfoType.builder()
                .templateComponentId(templateComponentId)
                .type(getType())
                .templateType(getType())
                .build();
    }

    public static List<SignUpFillInfoType> cloneToNewTemplateComponentId(List<SignUpFillInfoType> signUpFillInfoTypes, Map<Integer, Integer> oldNewTemplateComponentIdRelation) {
        return Optional.ofNullable(signUpFillInfoTypes).orElse(Lists.newArrayList()).stream().map(v -> v.cloneToNewTemplateComponentId(oldNewTemplateComponentIdRelation.get(v.getTemplateComponentId()))).collect(Collectors.toList());
    }

    @Getter
    public enum TypeEnum {

        /** 普通表单 */
        FORM("普通采集表单", "form"),
        WFW_FORM("万能表单", "wfw_form");

        private String name;
        private String value;

        TypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static TypeEnum fromValue(String value) {
            TypeEnum[] values = TypeEnum.values();
            for (TypeEnum typeEnum : values) {
                if (Objects.equals(typeEnum.getValue(), value)) {
                    return typeEnum;
                }
            }
            return null;
        }

    }

    /** 万能表单模版类型
     * @className SignUpFillInfoType
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-08-17 10:58:16
     * @version ver 1.0
     */
    @Getter
    public enum WfwFormTemplateEnum {

        /** 通用 */
        NORMAL("通用", "normal", "deptManager_hdcp", "SObtv7P3d$UVuBkTjg"),
        DUAL_SELECT_COMPANY("双选会企业报名", "dual_select_company", "deptManager_hdcp", "SObtv7P3d$UVuBkTjg");

        private final String name;
        private final String value;
        private final String sign;
        private final String key;

        WfwFormTemplateEnum(String name, String value, String sign, String key) {
            this.name = name;
            this.value = value;
            this.sign = sign;
            this.key = key;
        }

        public static WfwFormTemplateEnum fromValue(String value) {
            WfwFormTemplateEnum[] values = WfwFormTemplateEnum.values();
            for (WfwFormTemplateEnum wfwFormTemplateEnum : values) {
                if (Objects.equals(wfwFormTemplateEnum.getValue(), value)) {
                    return wfwFormTemplateEnum;
                }
            }
            return null;
        }
    }

}