package com.chaoxing.activity.dto.blacklist;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.model.BlacklistDetail;
import com.chaoxing.activity.util.DateUtils;
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
 * @description:
 * @author: huxiaolong
 * @date: 2022/3/2 5:35 下午
 * @version: 1.0
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistDetailDTO {

    /** 主键; column: id*/
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 姓名; column: user_name*/
    private String userName;
    /** 账号; column: account*/
    private String account;
    /** 违约信息; column: breach_content */
    private String breachContent;
    /** 违约时间; column: create_time*/
    private Long createTimestamp;
    /** 活动名称 */
    private String activityName;

    public static List<BlacklistDetailDTO> buildFromBlacklistDetail(List<BlacklistDetail> records) {
        return records.stream().map(BlacklistDetailDTO::buildFromBlacklist).collect(Collectors.toList());
    }

    public static BlacklistDetailDTO buildFromBlacklist(BlacklistDetail detail) {
        return BlacklistDetailDTO.builder()
                .id(detail.getId())
                .marketId(detail.getMarketId())
                .activityId(detail.getActivityId())
                .uid(detail.getUid())
                .userName(detail.getUserName())
                .account(detail.getAccount())
                .activityName(detail.getActivityName())
                .createTimestamp(DateUtils.date2Timestamp(detail.getCreateTime()))
                .build();
    }
}
