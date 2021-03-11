package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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
    private Boolean isAnonymous;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 是否被管理员删除; column: is_manager_deleted*/
    private Boolean isManagerDeleted;
    /** 审核状态。0：拒绝，1:通过，2:待审核; column: audit_status*/
    private Integer auditStatus;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

}