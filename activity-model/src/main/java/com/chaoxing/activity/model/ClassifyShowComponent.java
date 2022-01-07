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
 * 分类显示组件表
 * @className: ClassifyShowComponent, table_name: t_classify_show_component
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-01-05 15:26:34
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_classify_show_component")
public class ClassifyShowComponent {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 模版id; column: template_id*/
    private Integer templateId;
    /** 分类id; column: classify_id*/
    private Integer classifyId;
    /** 模版组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}