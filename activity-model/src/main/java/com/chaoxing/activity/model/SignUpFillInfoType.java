package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 报名填报信息类型表：默认、双选会、微服务表单
 * @className: SignUpFillInfoType, table_name: t_sign_up_fill_info_type
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:13
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_sign_up_fill_info_type")
public class SignUpFillInfoType {

    /** 主键; column: id*/
    private Integer id;
    /** 模版主键id; column: template_component_id*/
    private Integer templateComponentId;
    /** 类型; column: type*/
    private String type;



}