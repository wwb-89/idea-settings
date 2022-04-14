package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xhl
 * @version ver 1.0
 * @className ActivityRatingQueryDTO
 * @description
 * @blame xhl
 * @date 2021-03-11 11:54:14
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityRatingQueryDTO {

    /** 活动id */
    private Integer activityId;
    /** uid */
    private Integer uid;
    /** 审核状态。0：拒绝，1:通过，2:待审核*/
    private Integer auditStatus;
}
