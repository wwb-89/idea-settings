package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 任务详情
 * @className: ActivityStatTaskDetail, table_name: t_activity_stat_task_detail
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-05-10 16:08:51
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_stat_task_detail")
public class ActivityStatTaskDetail {

    /** 任务id; column: task_id*/
    @TableId(type = IdType.AUTO)
    private Integer taskId;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 失败次数; column: error_times*/
    private Integer errorTimes;
    /** 失败信息; column: error_message*/
    private String errorMessage;
    /** 处理状态。0：失败，1：成功，2：待处理; column: status*/
    private Integer status;

}