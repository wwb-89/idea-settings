package com.chaoxing.activity.vo.stat;

import com.chaoxing.activity.dto.stat.ActivityRegionalStatDTO;
import com.chaoxing.activity.model.ActivityStat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/11 3:24 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRegionalStatVo {

    private List<ActivityStat> topActivityStats;

    private List<ActivityRegionalStatDTO> regionalActivityStats;

    private List<ActivityRegionalStatDTO> orgActivityStats;
}
