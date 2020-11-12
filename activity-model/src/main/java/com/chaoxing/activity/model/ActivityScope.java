package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 活动参与范围
 * @className: ActivityScope, table_name: t_activity_scope
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_scope")
public class ActivityScope {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 机构id; column: hierarchy_id*/
    private Integer hierarchyId;
    /** 机构名称; column: name*/
    private String name;
    /** 层级pid; column: hierarchy_pid*/
    private Integer hierarchyPid;
    /** 编码; column: code*/
    private String code;
    /** 上级及当前名称组合; column: links*/
    private String links;
    /** 层级; column: level*/
    private Integer level;
    /** 修正后的层级; column: adjusted_level*/
    private Integer adjustedLevel;
    /** 参与机构id; column: fid*/
    private Integer fid;
    /** 是否包含子节点。0：否，1：是; column: is_exist_child*/
    @TableField(value = "is_exist_child")
    private Boolean existChild;
    /** 顺序; column: sort*/
    private Integer sort;

}