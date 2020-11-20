package com.chaoxing.activity.model;

import java.util.Date;

/**
 * 组附属机构表
 * @className: TGroupAffiliateOrg, table_name: t_group_affiliate_org
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-20 20:20:57
 * @version: ver 1.0
 */
public class TGroupAffiliateOrg {

    /** column: id*/
    private Integer id;
    /** 组id; column: group_id*/
    private Integer groupId;
    /** 附属机构fid; column: fid*/
    private Integer fid;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 更新时间; column: update_time*/
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}