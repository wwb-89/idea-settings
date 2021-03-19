package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动评价详情
 * @className: ActivityRatingDetail, table_name: t_activity_rating_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-08 16:07:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_rating_detail")
public class ActivityRatingDetail {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 打分人uid; column: scorer_uid*/
    private Integer scorerUid;
    /** 打分人姓名; column: scorer_user_name*/
    private String scorerUserName;
    /** 分值; column: score*/
    private BigDecimal score;
    /** 评论; column: comment*/
    private String comment;
    /** 是否匿名的; column: is_anonymous*/
    @TableField(value = "is_anonymous")
    private Boolean anonymous;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 是否被管理员删除; column: is_manager_deleted*/
    @TableField(value = "is_manager_deleted")
    private Boolean managerDeleted;
    /** 审核状态。0：拒绝，1:通过，2:待审核; column: audit_status*/
    private Integer auditStatus;
    /** 创建时间; column: create_time*/
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

    /** 审核状态枚举
     * @className ActivityRatingDetail
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-03-17 14:22:07
     * @version ver 1.0
     */
    @Getter
    public enum AuditStatus {

        /** 不通过 */
        REJECT("不通过", 0),
        PASSED("通过", 1),
        WAIT("待审核", 2);

        private String name;
        private Integer value;

        AuditStatus(String name, Integer value) {
            this.name = name;
            this.value = value;
        }

        public static AuditStatus fromValue(Integer value) {
            AuditStatus[] values = AuditStatus.values();
            for (AuditStatus auditStatus : values) {
                if (Objects.equals(auditStatus.getValue(), value)) {
                    return auditStatus;
                }
            }
            return null;
        }
    }

}