package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.activity.ActivityMenuDTO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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
    /** 是否启用; column: is_enable */
    @TableField(value = "is_enable")
    private Boolean enable;
    /** 是否系统菜单; column: is_system */
    @TableField(value = "is_system")
    private Boolean system;
    /** 自定义应用模板组件id; column: template_component_id */
    private Integer templateComponentId;

}