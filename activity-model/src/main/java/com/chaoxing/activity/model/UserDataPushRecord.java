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
 * 用户数据推送记录表
 * @className: UserDataPushRecord, table_name: t_user_data_push_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-10-29 14:25:53
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user_data_push_record")
public class UserDataPushRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 数据推送配置id; column: config_id*/
    private Integer configId;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 目标主键标识; column: target_identify*/
    private String targetIdentify;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

}