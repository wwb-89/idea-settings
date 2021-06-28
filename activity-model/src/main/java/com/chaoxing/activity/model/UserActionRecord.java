package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户行为表
 * @className: UserActionRecord, table_name: t_user_action_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-16 11:01:23
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_action_record")
public class UserActionRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 行为类型; column: action_type*/
    private String actionType;
    /** 具体行为; column: action*/
    private String action;
    /** 行为标识。行为id等; column: action_identify*/
    private String actionIdentify;
    /** 行为描述; column: action_description*/
    private String actionDescription;
    /** 是否有效的; column: is_valid*/
    @TableField(value = "is_valid")
    private Boolean valid;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    /** TODO 以下个人行为页面字段暂时放置，后续迁移其他实体 */
    @TableField(exist = false)
    private String title;
    @TableField(exist = false)
    private Integer way;
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private ActivityRatingDetail ratingDetail;

}