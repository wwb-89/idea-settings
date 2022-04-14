package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**时间范围对象
 * @author wwb
 * @version ver 1.0
 * @className StartEndTimeDTO
 * @description
 * @blame wwb
 * @date 2021-04-15 19:28:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeScopeDTO {

    /** 开始时间 */
    private LocalDateTime startTime;
    /** 结束时间 */
    private LocalDateTime endTime;

}