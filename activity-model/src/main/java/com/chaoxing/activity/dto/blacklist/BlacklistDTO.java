package com.chaoxing.activity.dto.blacklist;

import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.util.DateUtils;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**黑名单对象
 * @author wwb
 * @version ver 1.0
 * @className BlacklistDTO
 * @description
 * @blame wwb
 * @date 2021-07-27 16:06:33
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistDTO {

    /** 主键 */
    private Integer id;
    /** 市场id */
    private Integer marketId;
    /** 用户id */
    private Integer uid;
    /** 姓名 */
    private String userName;
    /** 账号 */
    private String account;
    /** 违约次数 */
    private Integer defaultNum;
    /** 有效小时数 */
    private Integer effectiveHours;
    /** 加入方式 */
    private String joinType;
    /** 创建时间 */
    private Long createTimestamp;

    public Blacklist buildBlacklist() {
        return Blacklist.builder()
                .id(getId())
                .marketId(getMarketId())
                .uid(getUid())
                .userName(getUserName())
                .account(getAccount())
                .defaultNum(getDefaultNum())
                .effectiveHours(getEffectiveHours())
                .joinType(getJoinType())
                .build();
    }

    public static List<Blacklist> buildBlacklist(List<BlacklistDTO> blacklistDtos) {
        return Optional.ofNullable(blacklistDtos).orElse(Lists.newArrayList()).stream().map(v -> v.buildBlacklist()).collect(Collectors.toList());
    }

    public static BlacklistDTO buildFromBlacklist(Blacklist blacklist) {
        return BlacklistDTO.builder()
                .id(blacklist.getId())
                .marketId(blacklist.getMarketId())
                .uid(blacklist.getUid())
                .userName(blacklist.getUserName())
                .account(blacklist.getAccount())
                .defaultNum(blacklist.getDefaultNum())
                .effectiveHours(blacklist.getEffectiveHours())
                .joinType(blacklist.getJoinType())
                .createTimestamp(DateUtils.date2Timestamp(blacklist.getCreateTime()))
                .build();
    }

    public static List<BlacklistDTO> buildFromBlacklist(List<Blacklist> blacklists) {
        return Optional.ofNullable(blacklists).orElse(Lists.newArrayList()).stream().map(BlacklistDTO::buildFromBlacklist).collect(Collectors.toList());
    }

}