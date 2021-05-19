package com.chaoxing.activity.dto.manager.sign;

import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.LocalDateTime2TimestampDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

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
                .deleted(true)
                .build();
    }

}