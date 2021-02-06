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
 * 机构表单关联
 * @className: OrgForm, table_name: t_org_form
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-02-06 17:52:32
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_form")
public class OrgForm {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 表单id; column: form_id*/
    private Integer formId;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}