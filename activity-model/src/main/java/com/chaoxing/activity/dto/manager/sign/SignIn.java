package com.chaoxing.activity.dto.manager.sign;

import com.alibaba.fastjson.annotation.JSONField;
import com.chaoxing.activity.util.LocalDateTime2TimestampDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 签到表
 * @className: SignIn
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-16 14:08:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignIn {

    /** 主键 */
    private Integer id;
    /** 报名签到id */
    private Integer signId;
    /** 签到名称 */
    private String name;
    /** 签到类型：签到、签退 */
    private String type;
    /** 签到码 */
    private String code;
    /** 开始时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime startTime;
    /** 结束时间 */
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTime2TimestampDeserializer.class)
    private LocalDateTime endTime;
    /** 签到方式。1：普通签到、2：位置签到、3：二维码签到 */
    private Integer way;
    /** 签到地址 */
    private String address;
    /** 详细地址 */
    private String detailAddress;
    /** 签到经度 */
    private BigDecimal longitude;
    /** 签到维度 */
    private BigDecimal dimension;
    /** 扫码方式。1：参与者扫码，2：参与者扫码 */
    private Integer scanCodeWay;
    /** 是否填写信息 */
    private Boolean fillInfo;
    /** 填写信息的表单id */
    private Integer fillInfoFormId;
    /** 是否公告签到名单 */
    private Boolean publicList;
    /** 按钮名称 */
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
    /** 创建机构id */
    private Integer createFid;
    /** 创建机构名称 */
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

}