package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图标
 * @className: Icon, table_name: t_icon
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-24 18:38:48
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_icon")
public class Icon {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private String name;
    /** 标签id; column: tag_id*/
    private String code;
    /** 默认图标的云盘id */
    private String defaultIconCloudId;
    /** 激活状态图标的云盘id */
    private String activeIconCloudId;
    /** 图标描述，用途 */
    private String description;

}