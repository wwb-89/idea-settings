package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateActivity
 * @description
 * @blame wwb
 * @date 2021-05-11 16:20:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormCreateActivity {

    /** 创建机构id */
    private Integer fid;
    /** 表单id */
    private Integer formId;
    /** 表单记录id */
    private Integer formUserId;
    /** 活动市场id */
    private Integer marketId;
    /** 活动标识 */
    private String flag;
    /** 使用的门户模版id */
    private Integer webTemplateId;

}
