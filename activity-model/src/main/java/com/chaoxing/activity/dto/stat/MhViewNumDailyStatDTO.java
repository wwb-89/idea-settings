package com.chaoxing.activity.dto.stat;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**门户访问量按日统计对象
 * @author wwb
 * @version ver 1.0
 * @className MhViewNumDailyStatDTO
 * @description
 * @blame wwb
 * @date 2021-04-15 19:21:20
 */
@Data
public class MhViewNumDailyStatDTO extends DailyStatDTO {

    /** 日期字符串 */
    @JSONField(name = "time")
    private String dateStr;
    /** 值 */
    @JSONField(name = "uv")
    private String value;

}