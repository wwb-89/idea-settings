package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 登录定制
 * @className: LoginCustom, table_name: t_login_custom
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-01-21 14:37:32
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_login_custom")
public class LoginCustom {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 区域编码。与fid互斥; column: area_code*/
    private String areaCode;
    /** fid。与区域编码互斥; column: fid*/
    private Integer fid;
    /** 登录地址; column: login_url*/
    private String loginUrl;
    /** 编码次数; column: encode_num*/
    private Integer encodeNum;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 更新时间; column: update_time*/
    private Date updateTime;

}