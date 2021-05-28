package com.chaoxing.activity.dto.manager;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xhl
 * @version ver 1.0
 * @className WfwGroupDTO
 * @description
 * @blame xhl
 * @date 2021-03-10 15:01:28
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwGroupDTO {
    /** 组id*/
    private String id;
    /** 组上一级id*/
    private String gid;
    /** 名称*/
    private String groupname;
    /** 子结点个数 */
    private Integer soncount;
    /** level */
    private Integer groupLevel;
}
