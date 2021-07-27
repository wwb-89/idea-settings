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
 * 黑名单规则表
 * @className: TBlacklistRule, table_name: t_blacklist_rule
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-26 18:45:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_blacklist_rule")
public class BlacklistRule {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 未签到上限; column: not_sign_in_upper_limit*/
    private Integer notSignInUpperLimit;
    /** 是否启用自动移除; column: is_enable_auto_remove*/
    @TableField(value = "is_enable_auto_remove")
    private Boolean enableAutoRemove;
    /** 自动移除天数; column: auto_remove_days*/
    private Integer autoRemoveDays;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}