package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.util.LocalDateTimeDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动表
 * @className: Activity, table_name: t_activity
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity")
public class Activity {

    /** 活动id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动名称; column: name*/
    private String name;
    /** 开始时间; column: start_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime startTime;
    /** 结束时间; column: end_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime endTime;
    /** 封面云盘id; column: cover_cloud_id*/
    private String coverCloudId;
    /** 主办方; column: organisers*/
    private String organisers;
    /** 活动形式; column: activity_type*/
    private String activityType;
    /** 活动地址; column: address*/
    private String address;
    /** 经度; column: longitude*/
    private BigDecimal longitude;
    /** 维度; column: dimension*/
    private BigDecimal dimension;
    /** 活动分类id; column: activity_classify_id*/
    private Integer activityClassifyId;
    /** 是否启用签到报名; column: is_enable_sign*/
    @TableField(value = "is_enable_sign")
    private Boolean enableSign;
    /** 签到报名id; column: sign_id*/
    private Integer signId;
    /** 网页模板id; column: web_template_id*/
    private Integer webTemplateId;
    /** 门户网页id; column: page_id*/
    private Integer pageId;
    /** 门户预览地址; column: preview_url*/
    private String previewUrl;
    /** 门户编辑地址; column: edit_url*/
    private String editUrl;
    /** 是否已发布; column: is_released*/
    @TableField(value = "is_released")
    private Boolean released;
    /** 发布时间; column: release_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime releaseTime;
    /** 发布人id; column: release_uid*/
    private Integer releaseUid;
    /** 状态。0：已删除，1：待发布，2：已发布，3：进行中，4：已结束; column: status*/
    private Integer status;
    /** 是否开启审核; column: is_open_audit*/
    @TableField(value = "is_open_audit")
    private Boolean openAudit;
    /** 审核状态。0：审核不通过，1：审核通过，2：待审核; column: audit_status*/
    private Integer auditStatus;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 创建人姓名; column: create_user_name*/
    private String createUserName;
    /** 创建单位id; column: create_fid*/
    private Integer createFid;
    /** 创建机构名; column: create_org_name*/
    private String createOrgName;
    /** 创建时间; column: create_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    /** 修改时间; column: update_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

    // 附加
    /** 开始时间字符串 */
    @TableField(exist = false)
    private String startTimeStr;
    /** 结束时间字符串 */
    @TableField(exist = false)
    private String endTimeStr;

    @Getter
    public enum StatusEnum {
        /** 已删除 */
        DELETED("已删除", 0),
        WAIT_RELEASE("待发布", 1),
        RELEASED("已发布", 2),
        ONGOING("进行中", 3),
        ENDED("已结束", 4);

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
            throw new BusinessException("未知的活动状态");
        }
    }

    @Getter
    public enum AuditStatusEnum {
        /** 已删除 */
        NOT_PASS("未通过", 0),
        PASSED("已通过", 1),
        WAIT_AUDIT("待审核", 2);

        private String name;
        private Integer value;

        AuditStatusEnum(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static AuditStatusEnum fromValue(Integer value) {
            AuditStatusEnum[] values = AuditStatusEnum.values();
            for (AuditStatusEnum auditStatusEnum : values) {
                if (Objects.equals(auditStatusEnum.getValue(), value)) {
                    return auditStatusEnum;
                }
            }
            throw new BusinessException("未知的审核状态");
        }

    }

}