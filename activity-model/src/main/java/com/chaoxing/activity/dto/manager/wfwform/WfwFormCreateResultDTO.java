package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**万能表单创建结果对象
 * @author wwb
 * @version ver 1.0
 * @className WfwFormCreateResultDTO
 * @description
 * @blame wwb
 * @date 2021-11-18 17:00:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormCreateResultDTO {

    /** 表单id */
    private Integer formId;
    /** 移动端填写页地址 */
    private String openAddr;
    /** pc端填写页地址 */
    private String pcUrl;
    /** 微信端填写地址 */
    private String wechatUrl;
    /** 表单编辑地址 */
    private String editUrl;

}