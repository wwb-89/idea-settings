package com.chaoxing.activity.dto.manager.wfwform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**表单过滤用到的text:value的键值对实体
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/31 3:44 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormFilterDTO {

    private String text;

    private String value;
}
