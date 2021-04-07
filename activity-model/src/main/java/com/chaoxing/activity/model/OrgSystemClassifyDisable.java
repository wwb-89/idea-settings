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
 * 机构系统分类禁用表
 * @className: OrgSystemClassifyDisable, table_name: t_org_system_classify_disable
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-04-06 16:02:32
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_system_classify_disable")
public class OrgSystemClassifyDisable {

    /** 机构id; column: fid*/
    @TableId(type = IdType.AUTO)
    private Integer fid;
    /** 分类id; column: classify_id*/
    private Integer classifyId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;

}