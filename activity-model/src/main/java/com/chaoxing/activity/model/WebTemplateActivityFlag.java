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
 * 活动标识与门户模版之间的关系
 * @className: WebTemplateActivityFlag, table_name: t_web_template_activity_flag
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-30 16:52:58
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_web_template_activity_flag")
public class WebTemplateActivityFlag {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动标识; column: activity_flag*/
    private String activityFlag;
    /** 模版id; column: web_template_id*/
    private Integer webTemplateId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}