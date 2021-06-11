package com.chaoxing.activity.dto.query.admin;

import com.chaoxing.activity.dto.OrgDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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

    private Integer fid;

    private List<Integer> activityIds;

    private List<OrgDTO> regions;

    private String startDate;

    private String endDate;


}
