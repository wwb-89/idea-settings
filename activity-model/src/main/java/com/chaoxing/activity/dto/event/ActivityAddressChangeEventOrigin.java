package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** 活动地点改变事件源
 * @author wwb
 * @version ver 1.0
 * @className ActivityAddressChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-09-28 11:12:42
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityAddressChangeEventOrigin extends AbstractEventOrigin {

    /** 活动id */
    private Integer activityId;
    /** 旧地址 */
    private String oldAddress;
    /** 新地址 */
    private String newAddress;
    /** 时间戳 */
    private Long timestamp;

}