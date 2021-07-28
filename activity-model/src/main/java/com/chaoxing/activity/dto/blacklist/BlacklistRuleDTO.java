package com.chaoxing.activity.dto.blacklist;

import com.chaoxing.activity.model.BlacklistRule;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**黑名单规则对象
 * @author wwb
 * @version ver 1.0
 * @className BlacklistRuleDTO
 * @description
 * @blame wwb
 * @date 2021-07-27 14:32:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BlacklistRuleDTO {

    /** 活动市场id */
    private Integer marketId;
    /** 未签到上限 */
    private Integer notSignInUpperLimit;
    /** 是否启用自动移除 */
    private Boolean enableAutoRemove;
    /** 自动移除小时数 */
    private Integer autoRemoveHours;

    public BlacklistRule buildBlacklistRule() {
        return BlacklistRule.builder()
                .marketId(getMarketId())
                .notSignInUpperLimit(getNotSignInUpperLimit())
                .enableAutoRemove(getEnableAutoRemove())
                .autoRemoveHours(getAutoRemoveHours())
                .build();
    }

    public static BlacklistRuleDTO buildFromBlacklistRule(BlacklistRule blacklistRule) {
        return BlacklistRuleDTO.builder()
                .marketId(blacklistRule.getMarketId())
                .notSignInUpperLimit(blacklistRule.getNotSignInUpperLimit())
                .enableAutoRemove(blacklistRule.getEnableAutoRemove())
                .autoRemoveHours(blacklistRule.getAutoRemoveHours())
                .build();
    }

    public static BlacklistRuleDTO buildDefault(Integer marketId) {
        return BlacklistRuleDTO.builder()
                .marketId(marketId)
                .enableAutoRemove(false)
                .build();
    }

}