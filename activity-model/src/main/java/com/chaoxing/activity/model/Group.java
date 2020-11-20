package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 组
 * @className: Group, table_name: t_group
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-20 20:20:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_group")
public class Group {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 组名; column: name*/
    private String name;
    /** 编码; column: code*/
    private String code;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 更新时间; column: update_time*/
    private Date updateTime;

}