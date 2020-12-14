package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户表单填写记录表
 * @className: UserForm, table_name: t_user_form
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-11 20:39:46
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_form")
public class UserForm {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 活动id; column: activity_form_id*/
    private Integer activityFormId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}