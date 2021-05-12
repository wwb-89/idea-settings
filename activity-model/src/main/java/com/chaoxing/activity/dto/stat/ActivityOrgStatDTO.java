package com.chaoxing.activity.dto.stat;

import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/10 3:19 下午
 * <p>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityOrgStatDTO {
    /** 活动个数 */
    private Integer activityNum;
    /** 浏览人数 */
    /** private Integer uv;*/
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
    /** 活动类型占比统计list */
    private List<ActivityClassifyDTO> classifyStatList;
    /** 机构fid下的活动id */
    private List<Integer> activityIds;

    public static ActivityOrgStatDTO buildDefault() {
        return ActivityOrgStatDTO.builder()
                .activityNum(0)
                .pv(0)
                .signedUpNum(0)
                .signedInNum(0)
                .daily(Lists.newArrayList())
                .pvTrend(Lists.newArrayList())
                .signUpTrend(Lists.newArrayList())
                .signInTrend(Lists.newArrayList())
                .classifyStatList(Lists.newArrayList())
                .activityIds(Lists.newArrayList())
                .build();
    }
}
