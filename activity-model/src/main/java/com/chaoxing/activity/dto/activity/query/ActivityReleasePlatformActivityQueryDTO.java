package com.chaoxing.activity.dto.activity.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动发布平台查询
 * @author wwb
 * @version ver 1.0
 * @className ActivityReleasePlatformActivityQueryDTO
 * @description
 * @blame wwb
 * @date 2021-12-02 15:43:55
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityReleasePlatformActivityQueryDTO {

    /** 区域code */
    private String code;
    /** 机构id */
    private Integer fid;
    /** 活动标识 */
    private String flag;
    /** 开始时间 */
    private Long startTimestamp;
    /** 结束时间 */
    private Long endTimestamp;

}