package com.chaoxing.activity.dto.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className SignChangeEventOrigin
 * @description
 * @blame wwb
 * @date 2021-10-27 19:33:34
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignChangeEventOrigin extends AbstractEventOrigin {

    private Integer signId;
    /** 事件发生的事件（时间戳） */
    private Long timestamp;

}
