package com.chaoxing.activity.dto.activity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/12 14:57
 * <p>
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityCreateDTO {

    /** 机构id*/
    private Integer fid;
    /** 市场id*/
    private Integer marketId;
    /** 分享活动的机构id集合，多个用逗号分割 */
    private String sharedFids;
    /** 活动信息实体，具体字段见下*/
    private ActivityCreateParamDTO activityInfo;
    /** 是否开启报名*/
    private Boolean openSignUp;
    /** 是否开启报名信息填写*/
    private Boolean openFillFormInfo;
    /** 报名信息填写字段集合，逗号分隔,如："姓名,学号,手机号" */
    private String fillFormInfo;
    /** 创建活动者uid */
    private Integer uid;


}
