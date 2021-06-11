package com.chaoxing.activity.dto.manager.sign;

import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.LocalDateTime2TimestampDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 报名
 * @className: SignUp
 * @Description:
 * @author: mybatis generator
 * @date: 2020-12-16 14:08:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUp {

    /** 主键 */
    private Integer id;
    /** 报名签到id */
    private Integer signId;
    /** 报名名称 */
    private String name;
    /** 是否开启审批 */
    private Boolean openAudit;
    /** 开始时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime startTime;
    /** 结束时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime endTime;
    /** 是否限制人数 */
    private Boolean limitPerson;
    /** 人数限制 */
    private Integer personLimit;
    /** 是否填写信息 */
    private Boolean fillInfo;
    /** 填写信息的表单id */
    private Integer fillInfoFormId;
    /** 是否公开报名名单 */
    private Boolean publicList;
    /** 报名按钮名称 */
    private String btnName;
    /** 是否报名结束后允许取消报名 */
    private Boolean endAllowCancel;
    /** 是否启用限制参与范围 */
    private Boolean enableLimitParticipateScope;
    /** 是否限制参与范围 */
    private Boolean limitParticipateScope;
    /** 参与范围限制类型 */
    private String limitParticipateScopeType;
    /** 活动标示 */
    private String activityFlag;
    /** 定制报名类型*/
    private String customSignUpType;
    /** 状态。1：未开始，2：进行中，3：已结束 */
    private Integer status;
    /** 是否被删除 */
    private Boolean deleted;
    /** 创建时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime createTime;
    /** 创建人id */
    private Integer createUid;
    /** 创建人姓名 */
    private String createUserName;
    /** 创建人fid */
    private Integer createFid;
    /** 创建人机构id */
    private String createOrgName;
    /** 更新时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime updateTime;
    /** 更新人id */
    private Integer updateUid;

    // 附加
    /** 开始时间字符串表示 */
    private Long startTimestamp;
    /** 结束时间字符串表示 */
    private Long endTimestamp;
    /** 参与范围 */
    private List<SignUpParticipateScope> participateScopes;

    /** 状态枚举
     * @className SignUp
     * @description
     * @author wwb
     * @blame wwb
     * @date 2021-03-29 11:33:26
     * @version ver 1.0
     */
    @Getter
    public enum StatusEnum {

        /** 未开始 */
        NOT_STARTED("未开始", 1),
        ONGOING("进行中", 2),
        ENDED("已结束", 3);

        private String name;
        private Integer value;

        StatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static StatusEnum fromValue(Integer value) {
            StatusEnum[] values = StatusEnum.values();
            for (StatusEnum statusEnum : values) {
                if (Objects.equals(statusEnum.getValue(), value)) {
                    return statusEnum;
                }
            }
            return null;
        }
    }

    /** 限制参与范围类型枚举
     * @className SignUp
     * @description
     * @author wwb
     * @blame wwb
     * @date 2021-03-29 15:46:32
     * @version ver 1.0
     */
    @Getter
    public enum LimitParticipateScopeType {

        /** 微服务组织架构 */
        WFW_ORGANIZATIONAL_STRUCTURE("微服务组织架构", "wfw_organizational_structure");

        private String name;
        private String value;

        LimitParticipateScopeType(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static LimitParticipateScopeType fromValue(String value) {
            LimitParticipateScopeType[] values = LimitParticipateScopeType.values();
            for (LimitParticipateScopeType limitParticipateScopeType : values) {
                if (Objects.equals(limitParticipateScopeType.getValue(), value)) {
                    return limitParticipateScopeType;
                }
            }
            return null;
        }
    }

    /**
     * @className SignUp
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-06-11 10:18:15
     * @version ver 1.0
     */
    @Getter
    public enum CustomSignUpTypeEnum {

        /** 双选会公司报名 */
        DUAL_SELECT_COMPANY("公司报名", "company");

        private String name;
        private String value;

        CustomSignUpTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static CustomSignUpTypeEnum fromValue(String value) {
            CustomSignUpTypeEnum[] values = CustomSignUpTypeEnum.values();
            for (CustomSignUpTypeEnum customSignUpTypeEnum : values) {
                if (Objects.equals(customSignUpTypeEnum.getValue(), value)) {
                    return customSignUpTypeEnum;
                }
            }
            return null;
        }

    }

    public static SignUp buildDefault() {
        LocalDateTime now = LocalDateTime.now();
        return SignUp.builder()
                .name("报名")
                .openAudit(false)
                .startTime(now)
                .endTime(now.plusMonths(1))
                .limitPerson(false)
                .personLimit(100)
                .fillInfo(false)
                .publicList(false)
                .btnName("报名参与")
                .endAllowCancel(true)
                .enableLimitParticipateScope(false)
                .limitParticipateScope(false)
                .activityFlag(Activity.ActivityFlag.NORMAL.getValue())
                .deleted(false)
                .build();
    }

}