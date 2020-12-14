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
 * 表单表
 * @className: ActivityForm, table_name: t_activity_form
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-11 20:39:46
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_form")
public class ActivityForm {

    /** id; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 表单选项id; column: form_field_id*/
    private Integer formFieldId;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人id; column: update_uid*/
    private Integer updateUid;

}