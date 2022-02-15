package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 自定义应用接口调用
 * @className: CustomAppInterfaceCall, table_name: t_custom_app_interface_call
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-02-15 10:34:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_custom_app_interface_call")
public class CustomAppInterfaceCall {

    /** 组件; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 自定义应用模版组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 接口地址; column: url*/
    private String url;
    /** 是否创建时调用; column: is_create_call*/
    @TableField(value = "is_create_call")
    private Boolean createCall;
    /** 是否发布时调用; column: is_release_call*/
    @TableField(value = "is_release_call")
    private Boolean releaseCall;
    /** 是否下架时调用; column: is_cancel_release_call*/
    @TableField(value = "is_cancel_release_call")
    private Boolean cancelReleaseCall;
    /** 是否开始时调用; column: is_start_call*/
    @TableField(value = "is_start_call")
    private Boolean startCall;
    /** 是否结束时调用; column: is_end_call*/
    @TableField(value = "is_end_call")
    private Boolean endCall;
    /** 是否删除时调用; column: is_delete_call*/
    @TableField(value = "is_delete_call")
    private Boolean deleteCall;

}