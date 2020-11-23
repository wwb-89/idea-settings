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
 * 组附属机构表
 * @className: GroupAffiliateOrg, table_name: t_group_affiliate_org
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-20 20:20:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_group_affiliate_org")
public class GroupAffiliateOrg {

    /** column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 组id; column: group_id*/
    private Integer groupId;
    /** 附属机构fid; column: fid*/
    private Integer fid;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 更新时间; column: update_time*/
    private Date updateTime;

}