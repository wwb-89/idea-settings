package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动市场表格字段关联表
 * @className: MarketTableField, table_name: t_market_table_field
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-30 15:25:06
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_market_table_field")
public class MarketTableField {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 表格字段配置id; column: table_field_id*/
    private Integer tableFieldId;
    /** 表格字段详细配置id; column: table_field_detail_id*/
    private Integer tableFieldDetailId;
    /** 是否置顶; column: is_top*/
    @TableField(value = "is_top")
    private Boolean top;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 源自定义组件id; column: origin_component_id*/
    private Integer originComponentId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

}