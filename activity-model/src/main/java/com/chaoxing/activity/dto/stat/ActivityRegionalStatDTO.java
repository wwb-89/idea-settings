package com.chaoxing.activity.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/11 3:16 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRegionalStatDTO {

    private Integer fid;
    /** 区域、机构名称 */
    private String name;
    /** 活动个数 */
    private Integer activityNum;
    /** 访问量 */
    private Integer pv;
    /** 报名数 */
    private Integer signedUpNum;
    /** 签到数 */
    private Integer signedInNum;

    public static ActivityRegionalStatDTO buildDefault(Integer fid, String name) {
        return ActivityRegionalStatDTO.builder()
                .fid(fid)
                .name(name)
                .activityNum(0)
                .pv(0)
                .signedUpNum(0)
                .signedInNum(0)
                .build();
    }
}
