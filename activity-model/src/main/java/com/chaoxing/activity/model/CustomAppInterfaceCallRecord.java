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
 * 自定义应用接口调用记录表
 * @className: CustomAppInterfaceCallRecord, table_name: t_custom_app_interface_call_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-02-15 10:34:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_custom_app_interface_call_record")
public class CustomAppInterfaceCallRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 自定义应用模版组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 调用时间; column: call_time*/
    private LocalDateTime callTime;
    /** 状态。0：失败，1：成功; column: status*/
    private Integer status;
    /** 错误原因; column: message*/
    private String message;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}