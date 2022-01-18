package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动自定义应用配置表
 * @className: ActivityAppConfig, table_name: t_activity_custom_app_config
 * @Description:
 * @author: mybatis generator
 * @date: 2021-01-17 09:33:22
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_custom_app_config")
public class ActivityCustomAppConfig {

    /** 主键id */
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id */
    private Integer activityId;
    /** 链接类型，frontend：前端，backend：后端; column: type */
    private String type;
    /** 链接标题; column: title */
    private String title;
    /** 图标id; column: icon_id */
    private String iconId;
    /** 链接; column: url */
    private String url;
    /** 是否pc端菜单; column: is_pc  */
    @TableField(value = "is_pc")
    private Boolean pc;
    /** 是否移动端菜单; column: is_mobile  */
    @TableField(value = "is_mobile")
    private Boolean mobile;
    /** 显示规则(no_limit, before_sign_up, after_sign_up); column: show_rule  */
    private String showRule;
    /** 是否删除; column: is_deleted */
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    /** 默认图标cloudId */
    @TableField(exist = false)
    private String defaultIconCloudId;
    /** 激活图标cloudId */
    @TableField(exist = false)
    private String activeIconCloudId;

}
