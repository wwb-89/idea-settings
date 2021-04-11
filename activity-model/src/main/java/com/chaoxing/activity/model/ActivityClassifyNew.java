package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * 活动类型表
 * @className: ActivityClassifyNew, table_name: t_activity_classify_new
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-04-11 22:00:29
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_classify_new")
public class ActivityClassifyNew {

    /** 主键; column: id*/
    @TableId
    private Integer id;
    /** 名称; column: name*/
    private String name;
    /** 活动市场id; column: activity_market_id*/
    private Integer activityMarketId;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

}