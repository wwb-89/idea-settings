package com.chaoxing.activity.dto.manager.group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**创建小组参数
 * @author wwb
 * @version ver 1.0
 * @className GroupCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-09-17 14:03:21
 */
@Getter
@AllArgsConstructor
@ToString
public class GroupCreateParamDTO {

    /** 小组名称 */
    private final String name;
    /** 当前用户uid */
    private final Integer puid;

}