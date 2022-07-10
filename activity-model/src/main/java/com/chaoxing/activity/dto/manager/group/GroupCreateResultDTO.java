package com.chaoxing.activity.dto.manager.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className GroupCreateResultDTO
 * @description
 * @blame wwb
 * @date 2021-09-17 14:00:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GroupCreateResultDTO {

    /** 小组id */
    private Integer id;
    /** 小组bbsid  */
    private String bbsid;
    /** 小组名称 */
    private String name;
    /** 小组logo */
    private String logo;

}
