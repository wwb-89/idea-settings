package com.chaoxing.activity.dto.work;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**作品征集按钮
 * @author wwb
 * @version ver 1.0
 * @className WorkBtnDTO
 * @description
 * @blame wwb
 * @date 2021-09-17 16:59:18
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkBtnDTO {

    /** 按钮名称 */
    private String buttonName;
    /** 链接地址 */
    private String linkUrl;
    /** 是否可用 */
    private Boolean enable;
    /** 是否需要验证 */
    private Boolean needValidate;

}