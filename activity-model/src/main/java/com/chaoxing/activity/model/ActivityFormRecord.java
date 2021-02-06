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
 * 活动表单记录
 * @className: ActivityFormRecord, table_name: t_activity_form_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-02-06 17:52:32
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_form_record")
public class ActivityFormRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 表单id; column: form_id*/
    private Integer formId;
    /** 用户填写的表单行id; column: form_user_Id*/
    private Integer formUserId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 修改时间; column: update_time*/
    private LocalDateTime updateTime;

}