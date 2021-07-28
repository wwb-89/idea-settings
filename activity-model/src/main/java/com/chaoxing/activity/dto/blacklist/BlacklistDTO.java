package com.chaoxing.activity.dto.blacklist;

import com.chaoxing.activity.model.Blacklist;
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
    /** 加入方式 */
    private String joinType;

    public Blacklist buildBlacklist() {
        return Blacklist.builder()
                .id(getId())
                .marketId(getMarketId())
                .uid(getUid())
                .userName(getUserName())
                .account(getAccount())
                .defaultNum(getDefaultNum())
                .joinType(getJoinType())
                .build();
    }

    public static List<Blacklist> buildBlacklist(List<BlacklistDTO> blacklistDtos) {
        return Optional.ofNullable(blacklistDtos).orElse(Lists.newArrayList()).stream().map(v -> v.buildBlacklist()).collect(Collectors.toList());
    }

}