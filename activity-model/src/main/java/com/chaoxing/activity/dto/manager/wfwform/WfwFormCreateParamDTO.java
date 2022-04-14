package com.chaoxing.activity.dto.manager.wfwform;

import com.chaoxing.activity.model.SignUpWfwFormTemplate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**万能表单创建参数对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateParamDTO
 * @description
 * @blame wwb
 * @date 2021-11-18 16:56:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormCreateParamDTO {

    /** 来源表单id */
    private Integer formId;
    /** 来源表单的机构id */
    private Integer originalFid;
    /** 表单类型： 0-审批， 2：表单(默认) */
    private Integer formType;
    /** 创建用户id */
    private Integer uid;
    /** 创建机构id */
    private Integer fid;
    /** sign */
    private String sign;
    /** key */
    private String key;

}