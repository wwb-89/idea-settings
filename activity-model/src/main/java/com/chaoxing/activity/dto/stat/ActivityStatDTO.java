package com.chaoxing.activity.dto.stat;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**活动统计对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityStatDTO
 * @description
 * @blame wwb
 * @date 2021-04-15 19:58:30
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatDTO {

    /** 访问量 */
    private Integer pv;
    /** 报名数 */
    private Integer signedUpNum;
    /** 签到数 */
    private Integer signedInNum;
    /** 日期范围 */
    private List<String> daily;
    /** 访问量趋势 */
    private List<DailyStatDTO> pvTrend;
    /** 报名趋势 */
    private List<DailyStatDTO> signUpTrend;
    /** 签到趋势 */
    private List<DailyStatDTO> signInTrend;

    public static ActivityStatDTO buildDefault() {
        return ActivityStatDTO.builder()
                .pv(0)
                .signedUpNum(0)
                .signedInNum(0)
                .daily(Lists.newArrayList())
                .pvTrend(Lists.newArrayList())
                .signUpTrend(Lists.newArrayList())
                .signInTrend(Lists.newArrayList())
                .build();
    }

}