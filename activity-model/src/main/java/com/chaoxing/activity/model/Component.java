package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.sun.org.apache.xpath.internal.operations.Bool;
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
    /** 是否开关组件，0：否，1：是; column: is_switch_btn */
    @TableField(value = "is_switch_btn")
    private Boolean switchBtn;

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
        /** 活动系统组件 */
        ACTIVITY_NAME("名称", "activity_name"),
        ACTIVITY_TIME_SCOPE("活动时间", "activity_time_scope"),
        ACTIVITY_COVER("封面", "activity_cover"),
        ACTIVITY_ORGANISERS("主办方", "activity_organisers"),
        ACTIVITY_TYPE("类型", "activity_type"),
        ACTIVITY_CLASSIFY("校区", "activity_classify"),
        MAX_PARTICIPATE_TIME_LENGTH("最大参与时长", "max_participate_time_length"),
        INTEGRAL("积分", "integral"),
        ACTIVITY_RELEASE_SCOPE("发布范围", "activity_release_scope"),
        WORK("作品征集", "work"),
        ACTIVITY_RATING("活动评价", "activity_rating"),
        TIMING_RELEASE("定时发布", "timing_release"),
        INTRODUCTION("简介", "introduction"),
        SIGN_IN_OUT("签到", "sign_in_out"),
        PARTITION("分区", "partition"),
        READING("阅读", "reading"),
        GROUP("讨论小组", "group"),
        INSPECTION_CONFIG("考核设置", "inspection_config"),
        CLAZZ_INTERACTION("班级互动", "clazz_interaction"),
        CERTIFICATE("证书设置", "certificate"),
        PUSH_REMINDER("推送提醒", "push_reminder"),
        FORM_COLLECTION("表单采集", "form_collection"),
        SIGN_UP_ROLE_LIMIT("角色范围", "sign_up_role_limit"),

        /** 报名 */
        COMPANY_SIGN_UP("企业报名", "company_sign_up"),
        SIGN_UP("报名", "sign_up"),
        SIGN_UP_TIME_SCOPE("报名时间", "sign_up_time_scope"),
        WFW_PARTICIPATION_SCOPE("微服务参与范围", "wfw_participation_scope"),
        CONTACTS_PARTICIPATION_SCOPE("通讯录参与范围", "contacts_participation_scope"),
        SIGN_UP_PERSON_LIMIT("人数限制", "sign_up_person_limit"),
        SIGN_UP_FILL_INFO("报名填报信息", "sign_up_fill_info"),
        SIGN_UP_REVIEW("报名需要审核", "sign_up_review"),
        SIGN_UP_PUBLIC_LIST("报名名单公开", "sign_up_public_list"),
        SIGN_UP_CANCEL_SIGNED_UP("限制取消报名", "sign_up_cancel_signed_up"),
        SIGN_UP_CONDITION("报名条件", "sign_up_condition"),
        ON_SITE_SIGN_UP("现场报名", "on_site_sign_up");


        private final String name;
        private final String value;

        SystemComponentCodeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

}