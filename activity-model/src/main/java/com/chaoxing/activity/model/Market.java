package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.util.constant.DomainConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * 活动市场表
 * @className: Market, table_name: t_market
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-04-11 11:06:42
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_market")
public class Market {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场名称; column: name*/
    private String name;
    /** 图标云盘id; column: icon_cloud_id*/
    private String iconCloudId;
    /** 图标地址; column: icon_url*/
    private String iconUrl;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 是否启用通讯录; column: is_enable_contacts*/
    @TableField(value = "is_enable_contacts")
    private Boolean enableContacts;
    /** 是否启用组织架构; column: is_enable_organization*/
    @TableField(value = "is_enable_organization")
    private Boolean enableOrganization;
    /** 是否启用区域架构; column: is_enable_regional*/
    @TableField(value = "is_enable_regional")
    private Boolean enableRegional;
    /** 来源类型; column: origin_type*/
    private String originType;
    /** 来源; column: origin*/
    private String origin;
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

    /** 来源类型枚举
     * @className Market
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-11-25 15:55:17
     * @version ver 1.0
     */
    @Getter
    public enum OriginTypeEnum {

        /** 系统 */
        SYSTEM("系统", "system"),
        WFW("微服务", "wfw"),
        WFW_FORM("万能表单", "wfw_form");

        private final String name;
        private final String value;

        OriginTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

    }

    public void perfectCreator(OperateUserDTO operateUserDto) {
        Integer uid = operateUserDto.getUid();
        setCreateUid(uid);
        setUpdateUid(uid);
    }

    public void perfectSequence(Integer sequence) {
        setSequence(sequence);
    }

    public void updateValidate(OperateUserDTO operateUserDto) {
        Optional.ofNullable(getId()).orElseThrow(() -> new BusinessException("活动市场id不能为空"));
        Optional.ofNullable(getName()).filter(StringUtils::isNotBlank).orElseThrow(() -> new BusinessException("名称不能为空"));
        Optional.ofNullable(getFid()).filter(v -> Objects.equals(v, operateUserDto.getFid())).orElseThrow(() -> new BusinessException("无权限"));
    }

    public void bindWfwApp(Integer wfwAppId) {
        setOrigin(String.valueOf(wfwAppId));
    }

    public String buildAppUrl() {
        return DomainConstant.WEB +  "?marketId=" + getId();
    }

    public String buildPcUrl() {
        return buildAppUrl();
    }

    public String buildMarketManageUrl() {
        return DomainConstant.ADMIN + "/market/" + getId();
    }

    public static Market cloneMarket(Market originMarket, Integer fid) {
        Market cloneMarket = new Market();
        BeanUtils.copyProperties(originMarket, cloneMarket);
        cloneMarket.setFid(fid);
        cloneMarket.setOriginType(OriginTypeEnum.SYSTEM.getValue());
        cloneMarket.setCreateUid(null);
        cloneMarket.setCreateUid(null);
        cloneMarket.setCreateTime(null);
        cloneMarket.setUpdateUid(null);
        cloneMarket.setUpdateTime(null);
        return cloneMarket;
    }

}