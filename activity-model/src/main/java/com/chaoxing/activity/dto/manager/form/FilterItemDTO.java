package com.chaoxing.activity.dto.manager.form;

import lombok.*;

import java.util.List;
import java.util.Map;

/**高级检索筛选条件item
 * @author 胡小龙
 * @version ver 1.0
 * @className FilterItemDTO
 * @description
 * @date 2021-04-21 16:19:04
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class FilterItemDTO {

    /** 字段id， id可通过”获取表单结构“接口的返回值拿到
     * 针对系统字段,
     * 提交人： id固定为-1；
     * 提交时间: id固定为-3;
     * 更新时间: id固定为-4
     */
    private Integer id;

    /** 字段别名 */
    private String alias;

    /** 字段类型标识， 标识可通过”获取表单结构“接口的返回值拿到。 针对系统字段 ，提交人固定为contact； 提交时间和更新时间固定为dateinput*/
    private String compt;

    /** 查询条件的关系标识, 不同类型的字段支持不同的查询关系 */
    private String express;

    /** 查询关键词 */
    private String val;

    /** 当express等于><时使用该参数, 针对数字字段和日期字段, 可以使用该参数设置查询的范围 */
    private List<Map<String, String>> range;
}
