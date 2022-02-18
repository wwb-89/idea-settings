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
 * 活动标识默认系统菜单配置表
 * @className: FlagDefaultSystemMenuConfig, table_name: t_flag_default_system_menu_config
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-02-18 14:01:11
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_flag_default_system_menu_config")
public class FlagDefaultSystemMenuConfig {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动标识; column: flag*/
    private String flag;
    /** 菜单; column: menu*/
    private String menu;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 显示规则(no_limit, before_sign_up, after_sign_up); column: show_rule*/
    private String showRule;
    /** 是否删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}