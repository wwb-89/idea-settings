package com.chaoxing.activity.dto.manager.sign;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author xhl
 * @version ver 1.0
 * @className SignUpGroupRange
 * @description
 * @blame xhl
 * @date 2021-03-10 17:34:08
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignUpGroupRange {

    /** 报名id; column: sign_up_id*/
    private Integer signUpId;
    /** 组织架构id; column: id*/
    private Integer id;
    /** 组织架构父id; column: gid*/
    private Integer gid;
    /** 名称; column: groupname*/
    private String groupname;
    /** 1:机构 2:节点; column: type*/
    private String type;
}
