package com.chaoxing.activity.dto.stat;

import com.chaoxing.activity.model.BlacklistRecord;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**用户未签到数量统计
 * @author wwb
 * @version ver 1.0
 * @className UserNotSignedInNumStatDTO
 * @description
 * @blame wwb
 * @date 2021-07-27 18:31:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserNotSignedInNumStatDTO {

    /** 用户id */
    private Integer uid;
    /** 姓名 */
    private String userName;
    /** 账号 */
    private String account;
    /** 未签到数量 */
    private Integer notSignedInNum;

    public BlacklistRecord buildBlacklistRecord(Integer markId, Integer activityId) {
        return BlacklistRecord.builder()
                .marketId(markId)
                .activityId(activityId)
                .uid(getUid())
                .userName(getUserName())
                .account(getAccount())
                .notSignedInNum(getNotSignedInNum())
                .build();
    }

    public static List<BlacklistRecord> buildBlacklistRecord(List<UserNotSignedInNumStatDTO> userNotSignedInNumStatDtos, Integer marketId, Integer activityId) {
        return Optional.ofNullable(userNotSignedInNumStatDtos).orElse(Lists.newArrayList()).stream().map(v -> v.buildBlacklistRecord(marketId, activityId)).collect(Collectors.toList());
    }

}