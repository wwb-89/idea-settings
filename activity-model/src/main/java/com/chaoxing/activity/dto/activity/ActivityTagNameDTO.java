package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**活动标签名称对象
 * @author wwb
 * @version ver 1.0
 * @className ActivityTagNameDTO
 * @description
 * @blame wwb
 * @date 2021-11-25 09:50:46
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityTagNameDTO {

    private Integer activityId;
    private String tagName;

}