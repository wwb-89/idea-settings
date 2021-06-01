package com.chaoxing.activity.model;

import java.util.Date;

/**
 * 活动创建权限
 * @className: ActivityCreatePermission, table_name: t_activity_create_permission
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-01 18:33:22
 * @version: ver 1.0
 */
public class ActivityCreatePermission {

    /** 主键; column: id*/
    private Integer id;
    /** 所属机构id; column: fid*/
    private Integer fid;
    /** 角色id; column: role_id*/
    private Integer roleId;
    /** 是否全选; column: is_all_activity_classify*/
    private Boolean isAllActivityClassify;
    /** 活动类型范围，以","分割的活动类型id列表; column: activity_classify_scope*/
    private String activityClassifyScope;
    /** 报名范围类型; column: sign_up_scope_type*/
    private Integer signUpScopeType;
    /** 是否被删除; column: is_deleted*/
    private Boolean isDeleted;
    /** 创建时间; column: create_time*/
    private Date createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private Date updateTime;
    /** 更新人id; column: update_uid*/
    private Integer updateUid;
    /** 报名范围。以","分割的id列表; column: sign_up_scope*/
    private String signUpScope;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFid() {
        return fid;
    }

    public void setFid(Integer fid) {
        this.fid = fid;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public Boolean getIsAllActivityClassify() {
        return isAllActivityClassify;
    }

    public void setIsAllActivityClassify(Boolean isAllActivityClassify) {
        this.isAllActivityClassify = isAllActivityClassify;
    }

    public String getActivityClassifyScope() {
        return activityClassifyScope;
    }

    public void setActivityClassifyScope(String activityClassifyScope) {
        this.activityClassifyScope = activityClassifyScope;
    }

    public Integer getSignUpScopeType() {
        return signUpScopeType;
    }

    public void setSignUpScopeType(Integer signUpScopeType) {
        this.signUpScopeType = signUpScopeType;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getCreateUid() {
        return createUid;
    }

    public void setCreateUid(Integer createUid) {
        this.createUid = createUid;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUpdateUid() {
        return updateUid;
    }

    public void setUpdateUid(Integer updateUid) {
        this.updateUid = updateUid;
    }

    public String getSignUpScope() {
        return signUpScope;
    }

    public void setSignUpScope(String signUpScope) {
        this.signUpScope = signUpScope;
    }
}