package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动菜单配置表
 * @className: ActivityMenuConfig, table_name: t_activity_menu_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-08-06 16:01:41
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_menu_config")
public class ActivityMenuConfig {

    /** 主键; column: id*/
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 菜单; column: menu*/
    private String menu;

}