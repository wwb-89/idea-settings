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
 * 地区筛选表
 * @className: GroupRegionFilter, table_name: t_group_region_filter
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-20 18:07:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_group_region_filter")
public class GroupRegionFilter {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 组id; column: group_id*/
    private Integer groupId;
    /** 地区名称; column: name*/
    private String name;
    /** 管理机构id; column: manage_fid*/
    private Integer manageFid;
    /** 区域编码; column: code*/
    private String code;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}