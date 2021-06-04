package com.chaoxing.activity.dto.query.admin;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**用户统计汇总查询对象
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryQueryDTO
 * @description
 * @blame wwb
 * @date 2021-05-28 15:53:44
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatSummaryQueryDTO {

    /** 机构id */
    private Integer fid;
    /** 组织架构的级别 */
    private Integer groupLevel;
    /** 组织架构id */
    private Integer groupId;
    /** 关键字 */
    private String sw;
    /** 排序字段id */
    private Integer orderTableFieldId;
    /** 排序字段 */
    private String orderField;
    /** 排序方式 */
    private OrderTypeEnum orderType;
    /** 部门用户uid列表 */
    private List<Integer> groupUids;
    /** 机构用户uid列表 */
    private List<Integer> orgUids;
}
