package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**证书查询对象
 * @author wwb
 * @version ver 1.0
 * @className UserCertificateQueryDTO
 * @description
 * @blame wwb
 * @date 2021-12-16 11:27:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCertificateQueryDTO {

    /** 活动ID */
    private Integer activityId;
    /** 关键字 */
    private String sw;
    /** 排序字段 */
    private Integer status;
    /** 排序方式 */
    private OrderTypeEnum orderType;

}
