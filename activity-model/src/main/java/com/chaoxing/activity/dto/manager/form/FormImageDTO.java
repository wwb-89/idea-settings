package com.chaoxing.activity.dto.manager.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**万能表单图片对象
 * @author wwb
 * @version ver 1.0
 * @className FormImageDTO
 * @description
 * @blame wwb
 * @date 2021-12-20 18:33:07
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormImageDTO {

    /** 图片名称 */
    private String name;
    /** 图片云盘资源id */
    private String objectId;
    /** 图片后缀 */
    private String suffix;

}