package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 活动班级关联表
 * @className: ActivityClass, table_name: t_activity_class
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-09-02 17:03:27
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_class")
public class ActivityClass {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 班级id; column: class_id*/
    private Integer classId;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 更新时间; column: update_time*/
    private Date updateTime;

}