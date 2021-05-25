package com.chaoxing.activity.dto.stat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**每日统计对象
 * @author wwb
 * @version ver 1.0
 * @className DailyStatDTO
 * @description
 * @blame wwb
 * @date 2021-04-15 17:21:20
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyStatDTO {

    /** 日期字符串 */
    private String dateStr;
    /** 值 */
    private String value;

}