package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
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
    /** 报名的模版组件关联id; column: template_component_id*/
    private Integer templateComponentId;
    /** 表单/审批模板ids集合; column: wfw_form_template_ids */
    private String wfwFormTemplateIds;

    // 计算属性
    /** 模板字段设置时，所用到显示已选择的表单id */
    @TableField(exist = false)
    private List<Integer> formTemplateIds;
    /** 活动创建修改时，获取模板已选择的表单选项 */
    @TableField(exist = false)
    private List<SignUpWfwFormTemplate> templateOptions;

    public SignUpFillInfoType cloneToNewTemplateComponentId(Integer templateComponentId) {
        return SignUpFillInfoType.builder()
                .templateComponentId(templateComponentId)
                .wfwFormTemplateIds(getWfwFormTemplateIds())
                .build();
    }

    public static List<SignUpFillInfoType> cloneToNewTemplateComponentId(List<SignUpFillInfoType> signUpFillInfoTypes, Map<Integer, Integer> oldNewTemplateComponentIdRelation) {
        return Optional.ofNullable(signUpFillInfoTypes).orElse(Lists.newArrayList()).stream().map(v -> v.cloneToNewTemplateComponentId(oldNewTemplateComponentIdRelation.get(v.getTemplateComponentId()))).collect(Collectors.toList());
    }

    /** wfwFormTemplateIds 字段值转换为 formTemplateIds字段
     * @Description
     * @author huxiaolong
     * @Date 2022-02-23 11:06:54
     * @return
     */
    public void wfwFormTemplateIds2FormTemplateIds() {
        this.formTemplateIds = Lists.newArrayList();
        if (StringUtils.isNotBlank(this.wfwFormTemplateIds)) {
            this.formTemplateIds = Arrays.stream(this.wfwFormTemplateIds.split(",")).map(Integer::valueOf).collect(Collectors.toList());
        }
    }

    /** formTemplateIds 字段值转换为 wfwFormTemplateIds 字段
     * @Description
     * @author huxiaolong
     * @Date 2022-02-23 11:08:03
     * @return
     */
    public void formTemplateIds2WfwFormTemplateIds() {
        if (CollectionUtils.isNotEmpty(this.formTemplateIds)) {
            this.wfwFormTemplateIds = this.formTemplateIds.stream().sorted().map(String::valueOf).collect(Collectors.joining(","));
        }
    }

    /**
     * @Description
     * @author huxiaolong
     * @Date 2022-02-23 14:34:37
     * @param wfwFormTemplates
     * @return
     */
    public void packageWfwTemplateOptions(List<SignUpWfwFormTemplate> wfwFormTemplates) {
        this.templateOptions = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(this.formTemplateIds)) {
            this.templateOptions = wfwFormTemplates.stream().filter(v -> this.formTemplateIds.contains(v.getId())).collect(Collectors.toList());
        }
    }

    @Getter
    public enum TypeEnum {

        /** 普通表单 */
        FORM("普通采集表单", "form"),
        WFW_FORM("万能表单", "wfw_form"),
        APPROVAL("审批", "approval");

        private final String name;
        private final String value;

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

}