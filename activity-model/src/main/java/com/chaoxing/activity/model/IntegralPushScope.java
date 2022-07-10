package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 积分推送范围表
 * @className: IntegralPushScope, table_name: t_integral_push_scope
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-12-24 15:53:48
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_integral_push_scope")
public class IntegralPushScope {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 机构id; column: fid*/
    private Integer fid;

}