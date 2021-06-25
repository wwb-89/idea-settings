package com.chaoxing.activity.dto.manager.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wwb
 * @version ver 1.0
 * @className FormCreateActivity
 * @description
 * @blame wwb
 * @date 2021-05-11 16:20:05
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormCreateActivity {

    /** 创建机构id */
    private Integer fid;
    /** 表单id */
    private Integer formId;
    /** 表单记录id */
    private Integer formUserId;
    /** 活动标识 */
    private String flag;
    /** 使用的模版id */
    private Integer templateId;

}
