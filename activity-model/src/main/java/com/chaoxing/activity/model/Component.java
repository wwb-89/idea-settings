package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 组件表
 * @className: Component, table_name: t_component
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:08
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_component")
public class Component {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 父组件id; column: pid*/
    private Integer pid;
    /** 组件名称; column: name*/
    private String name;
    /** 组件编码。系统字段才有code; column: code*/
    private String code;
    /** 是否必填; column: is_required*/
    @TableField(value = "is_required")
    private Boolean required;
    /** 简介; column: introduction*/
    private String introduction;
    /** 是否是系统组件; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 是否支持多个组件; column: is_multi*/
    @TableField(value = "is_multi")
    private Boolean multi;
    /** 组件类型。自定义组件才有类型：文本、单选、多选; column: type*/
    private String type;
    /** 数据来源; column: data_origin*/
    private String dataOrigin;
    /** 来源主键; column: origin_identify*/
    private String originIdentify;
    /** 字段标识; column: field_flag*/
    private String fieldFlag;
    /** 模板; column: templateId*/
    private Integer templateId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

    @TableField(exist = false)
    private List<ComponentField> componentFields;
    @TableField(exist = false)
    private List<String> fieldValues;
    /** 自定义应用配置列表 */
    @TableField(exist = false)
    private List<CustomAppConfig> customAppConfigs;
    /** 被删除的自定义应用配置ids */
    @TableField(exist = false)
    private List<Integer> removeCustomAppConfigIds;
    /** 自定义应用接口配置列表 */
    @TableField(exist = false)
    private List<CustomAppInterfaceCall> customAppInterfaceCalls;
    /** 被删除的自定义应用接口配置ids */
    @TableField(exist = false)
    private List<Integer> removeInterfaceCallIds;

    /**获取自定义组件类型
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-27 16:21:52
    * @param 
    * @return java.util.List<java.lang.String>
    */
    public static List<String> listCustomComponentType() {
        return Arrays.stream(TypeEnum.values()).map(TypeEnum::getValue).collect(Collectors.toList());
    }

    /**获取除自定义应用外的自定义组件列表
     * @Description
     * @author huxiaolong
     * @Date 2022-01-10 11:17:29
     * @return
     */
    public static List<String> listCustomTypeWithoutCustomApp() {
        return Arrays.stream(TypeEnum.values()).map(TypeEnum::getValue).filter(value -> !Objects.equals(value, TypeEnum.CUSTOM_APP.getValue())).collect(Collectors.toList());
    }

    @Getter
    public enum TypeEnum {

        /** 文本 */
        TEXT("文本", "text"),
        INT("整数", "int"),
        DECIMAL("小数", "decimal"),
        RICH_TEXT("富文本", "rich_text"),
        RADIO("单选", "radio"),
        CHECKBOX("多选", "checkbox"),
        CUSTOM_APP("自定义应用", "custom_app");

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

        public static Boolean chooseType(String value) {
            return Objects.equals(value, TypeEnum.RADIO.getValue()) || Objects.equals(value, TypeEnum.CHECKBOX.getValue());
        }
    }

    @Getter
    public enum DataOriginEnum {

        /** 自定义 */
        CUSTOM("自定义", "custom"),
        FORM("表单", "form");

        private final String name;
        private final String value;

        DataOriginEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static DataOriginEnum fromValue(String value) {
            DataOriginEnum[] values = DataOriginEnum.values();
            for (DataOriginEnum dataOriginEnum : values) {
                if (Objects.equals(dataOriginEnum.getValue(), value)) {
                    return dataOriginEnum;
                }
            }
            return null;
        }
    }

    public boolean isSystemComponent() {
        return Optional.ofNullable(getSystem()).orElse(false);
    }

    /** 系统组件枚举
     * @className Component
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-08-20 17:54:40
     * @version ver 1.0
     */
    @Getter
    public enum SystemComponentCodeEnum {

        /** 报名条件 */
        SIGN_UP_CONDITION("报名条件", "sign_up_condition"),
        SIGN_UP_FILL_INFO("报名填报信息", "sign_up_fill_info"),
        COMPANY_SIGN_UP("企业报名", "company_sign_up"),
        SIGN_UP("报名", "sign_up"),
        SIGN_IN_OUT("签到", "sign_in_out");

        private final String name;
        private final String value;

        SystemComponentCodeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

}