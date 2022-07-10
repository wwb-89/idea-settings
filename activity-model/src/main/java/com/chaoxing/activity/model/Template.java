package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.dto.OperateUserDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 模版表
 * @className: Template, table_name: t_template
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:53:01
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_template")
public class Template {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 源模版id; column: origin_template_id*/
    private Integer originTemplateId;
    /** 模版名称; column: name*/
    private String name;
    /** 是否是系统模版; column: is_system*/
    @TableField(value = "is_system")
    private Boolean system;
    /** 活动标识; column: activity_flag*/
    private String activityFlag;
    /** 所属机构id; column: fid*/
    private Integer fid;
    /** 封面url; column: cover_url*/
    private String coverUrl;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 创建人uid; column: create_uid*/
    private Integer createUid;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;
    /** 更新人uid; column: update_uid*/
    private Integer updateUid;

    /**克隆到一个新的市场
     * @Description 
     * @author wwb
     * @Date 2021-07-14 17:33:40
     * @param marketId
     * @param fid
     * @return com.chaoxing.activity.model.Template
    */
    public Template cloneToNewMarket(Integer marketId, Integer fid) {
        return Template.builder()
                .marketId(marketId)
                .fid(fid)
                .originTemplateId(getId())
                .name(getName())
                .system(false)
                .activityFlag(getActivityFlag())
                .coverUrl(getCoverUrl())
                .build();
    }

    public void perfectCreator(OperateUserDTO operateUserDto) {
        setCreateUid(operateUserDto.getUid());
    }

}