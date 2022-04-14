package com.chaoxing.activity.dto.manager.group;

import lombok.*;

/**小组对象
 * @author wwb
 * @version ver 1.0
 * @className GroupDTO
 * @description
 * @blame wwb
 * @date 2021-09-17 14:22:55
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GroupDTO {

    private Integer id;
    private String name;
    private String bbsid;
    private String logo;
    private String inviteCodeUrl;
    private String shareUrl;
    private String rankUrl;
    private Integer createrPuid;
    private String createRealName;

}
