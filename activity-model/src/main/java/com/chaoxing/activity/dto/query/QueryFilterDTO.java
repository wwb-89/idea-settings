package com.chaoxing.activity.dto.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**查询过滤filter
 * @author wwb
 * @version ver 1.0
 * @className QueryFilterDTO
 * @description
 * @blame wwb
 * @date 2021-12-27 17:03:49
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QueryFilterDTO {

    private String value;
    private String text;

}