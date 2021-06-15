package com.chaoxing.activity.dto.stat;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**报名签到活动统计
 * @author wwb
 * @version ver 1.0
 * @className SignActivityStatDTO
 * @description
 * @blame wwb
 * @date 2021-04-16 10:00:02
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignActivityStatDTO {

    /** 报名数 */
    private Integer signedUpNum;
    /** 签到数 */
    private Integer signedInNum;
    /** 报名趋势 */
    private List<DailyStatDTO> signUpTrend;
    /** 签到趋势 */
    private List<DailyStatDTO> signInTrend;

    public static SignActivityStatDTO buildDefault() {
        return SignActivityStatDTO.builder()
                .signedUpNum(0)
                .signedInNum(0)
                .signUpTrend(Lists.newArrayList())
                .signInTrend(Lists.newArrayList())
                .build();
    }

}