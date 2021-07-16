package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 活动市场表
 * @className: ActivityMarket, table_name: t_activity_market
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-04-11 11:06:42
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_market")
public class ActivityMarket {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场名称; column: name*/
    private String name;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 微服务应用id; column: wfw_app_id*/
    private Integer wfwAppId;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 是否被删除; column: is_deleted*/
    @TableField(value = "is_deleted")
    private Boolean deleted;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人id; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人id; column: update_uid*/
    private Integer updateUid;

    public void perfectCreator(OperateUserDTO operateUserDto) {
        setCreateUid(operateUserDto.getUid());
    }

    public void updateValidate(OperateUserDTO operateUserDto) {
        Optional.ofNullable(getId()).orElseThrow(() -> new BusinessException("活动市场id不能为空"));
        Optional.ofNullable(getName()).filter(StringUtils::isNotBlank).orElseThrow(() -> new BusinessException("名称不能为空"));
        Optional.ofNullable(getFid()).filter(v -> Objects.equals(v, operateUserDto.getFid())).orElseThrow(() -> new BusinessException("无权限"));
    }

}