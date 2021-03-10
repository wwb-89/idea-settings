package com.chaoxing.activity.dto.manager.sign;

import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.util.LocalDateTime2TimestampDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 报名表
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
    /** 参与范围 1:不限 2：自定义*/
    private Integer joinRange;

    // 附加
    /** 开始时间字符串表示 */
    private Long startTimestamp;
    /** 结束时间字符串表示 */
    private Long endTimestamp;
    /** 选中范围 */
    private String groupsJsonStr;

}