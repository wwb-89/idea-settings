package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.manager.WfwAreaDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 活动发布范围
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

    public static List<WfwAreaDTO> convert2WfwRegionalArchitectures(List<ActivityScope> activityScopes) {
        List<WfwAreaDTO> wfwRegionalArchitectures = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(activityScopes)) {
            for (ActivityScope activityScope : activityScopes) {
                WfwAreaDTO wfwRegionalArchitecture = WfwAreaDTO.builder()
                        .id(activityScope.getHierarchyId())
                        .name(activityScope.getName())
                        .pid(activityScope.getHierarchyPid())
                        .code(activityScope.getCode())
                        .links(activityScope.getLinks())
                        .level(activityScope.getLevel())
                        .fid(activityScope.getFid())
                        .existChild(activityScope.getExistChild())
                        .sort(activityScope.getSort())
                        .build();
                wfwRegionalArchitectures.add(wfwRegionalArchitecture);
            }
        }
        return wfwRegionalArchitectures;
    }

}