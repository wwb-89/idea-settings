package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 黑名单记录表
 * @className: TBlacklistRecord, table_name: t_blacklist_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-26 18:45:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_blacklist_record")
public class BlacklistRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 姓名; column: user_name*/
    private String userName;
    /** 账号; column: account*/
    private String account;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 未签次数; column: not_signed_in_num*/
    private Integer notSignedInNum;
    /** 是否已处理; column: is_handled*/
    @TableField(value = "is_handled")
    private Boolean handled;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    public Blacklist buildBlacklist() {
        return Blacklist.builder()
                .marketId(getMarketId())
                .uid(getUid())
                .userName(getUserName())
                .account(getAccount())
                .defaultNum(getNotSignedInNum())
                .joinType(Blacklist.JoinTypeEnum.AUTO.getValue())
                .build();
    }

    public static List<Blacklist> buildbuildBlacklist(List<BlacklistRecord> blacklistRecords) {
        return Optional.ofNullable(blacklistRecords).orElse(Lists.newArrayList()).stream().map(v -> v.buildBlacklist()).collect(Collectors.toList());
    }

}