package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动与活动市场关联表
 * @className: TActivityMarket, table_name: t_activity_market
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-08-09 19:17:42
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_market")
public class ActivityMarket {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 活动状态; column: status*/
    private Integer status;
    /** 是否发布; column: is_released*/
    @TableField(value = "is_released")
    private Boolean released;
    /** 是否置顶; column: is_top*/
    @TableField(value = "is_top")
    private Boolean top;

}