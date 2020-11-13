package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户表
 * @className: User, table_name: t_user
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-12 17:44:51
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_user")
public class User {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 真实姓名; column: real_name*/
    private String realName;
    /** 登录名; column: login_name*/
    private String loginName;
    /** 手机号; column: mobile*/
    private String mobile;

}