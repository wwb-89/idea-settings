package com.chaoxing.activity.dto.query.admin;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/31 10:04 上午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatSummaryQueryDTO {

    private Integer fid;

    private Integer marketId;

    private String name;

    private Integer orderFieldId;

    private String orderField;

    private OrderTypeEnum orderType;

    private List<Integer> externalIds;

    private String startTime;

    private String endTime;

    private Integer activityStatus;

    private Integer activityClassifyId;


}
