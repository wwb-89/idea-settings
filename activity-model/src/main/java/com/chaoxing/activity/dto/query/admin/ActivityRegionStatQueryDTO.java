package com.chaoxing.activity.dto.query.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/11 3:51 下午
 * <p>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRegionStatQueryDTO {

    private Integer regionId;

    private Integer fid;

    private String startDate;

    private String endDate;
}
