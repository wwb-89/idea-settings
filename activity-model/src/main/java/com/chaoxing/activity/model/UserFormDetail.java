package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表单详情
 * @className: UserFormDetail, table_name: t_user_form_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-11 20:39:46
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_form_detail")
public class UserFormDetail {

    /** id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 活动表单id; column: activity_form_id*/
    private Integer activityFormId;
    /** 字段id; column: field_id*/
    private Integer fieldId;
    /** 值; column: value*/
    private String value;

}