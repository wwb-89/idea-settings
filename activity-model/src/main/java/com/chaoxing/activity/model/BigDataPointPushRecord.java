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
 * 大数据积分推送记录表
 * @className: BigDataPointPushRecord, table_name: t_big_data_point_push_record
 * @Description: 举办活动数据不存放在这个表里面
 * @author: mybatis generator
 * @date: 2021-10-13 11:08:51
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_big_data_point_push_record")
public class BigDataPointPushRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 积分类型; column: point_type*/
    private Integer pointType;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

}