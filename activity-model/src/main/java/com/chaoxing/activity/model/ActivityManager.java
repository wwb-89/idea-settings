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
 * 活动管理人员表
 * @className: ActivityManager, table_name: t_activity_manager
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-03-18 10:30:33
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_manager")
public class ActivityManager {

    /** 活动id; column: activity_id*/
    @TableId(type = IdType.AUTO)
    private Integer activityId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 用户姓名; column: user_name*/
    private String userName;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;

}