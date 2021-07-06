package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 组件表
 * @className: Component, table_name: t_component
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:08
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_component")
public class Component {

    /** 主键; column: id*/
    private Integer id;
    /** 市场id。系统组件为空; column: market_id*/
    private Integer marketId;
    /** 组件名称; column: name*/
    private String name;
    /** 组件编码。系统字段才有code; column: code*/
    private String code;
    /** 是否必填; column: is_required*/
    private Boolean isRequired;
    /** 简介; column: introduction*/
    private String introduction;
    /** 是否是系统组件; column: is_system*/
    private Boolean isSystem;
    /** 是否支持多个组件; column: is_multi*/
    private Boolean isMulti;
    /** 组件类型。自定义组件才有类型：文本、单选、多选; column: type*/
    private String type;
    /** 数据来源; column: data_origin*/
    private String dataOrigin;
    /** 来源主键; column: origin_identify*/
    private String originIdentify;
    /** 字段标识; column: field_flag*/
    private String fieldFlag;
    /** 所属机构id; column: fid*/
    private Integer fid;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private Date updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;
}