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
 * 报名万能表单模版表
 * @className: SignUpWfwFormTemplate, table_name: t_sign_up_wfw_form_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-18 16:09:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sign_up_wfw_form_template")
public class SignUpWfwFormTemplate {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 名称; column: name*/
    private String name;
    /** 编码; column: code*/
    private String code;
    /** sign; column: sign*/
    private String sign;
    /** key; column: key*/
    @TableField(value = "`key`")
    private String key;
    /** 表单id; column: form_id*/
    private Integer formId;
    /** 表单所属机构id; column: fid*/
    private Integer fid;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 是否系统表单; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}