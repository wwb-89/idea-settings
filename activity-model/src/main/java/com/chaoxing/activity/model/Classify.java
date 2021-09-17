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
 * 活动分类表
 * @className: Classify, table_name: t_classify
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_classify")
public class Classify {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 分类名称; column: name*/
    private String name;
    /** 是否是系统分类; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    /* 是否是本机构或市场拥有 */
    @TableField(exist = false)
    private Boolean owner;

    public static Classify buildFromName(String classifyName) {
        return Classify.builder()
                .name(classifyName)
                .system(false)
                .build();
    }

}