package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/2 11:15 上午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgRoleDTO {

    private Integer roleid;

    private String roleName;

    private Boolean checked;
}
