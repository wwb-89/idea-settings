package com.chaoxing.activity.dto.query;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.*;

import java.util.Objects;

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
    /** 考核状态 */
    private Integer qualifiedStatus;
    /** 排序字段 */
    private Integer orderFieldId;
    /** 排序方式 */
    private OrderTypeEnum orderType;

    // 计算出的值
    /** 排序字段 */
    private String orderField;


    /** 排序字段
     * @className UserCertificateQueryDTO
     * @description 
     * @author wwb
     * @blame wwb
     * @date 2021-12-20 11:18:21
     * @version ver 1.0
     */
    @Getter
    public enum OrderFieldEnum {

        /** 签到次数 */
        SIGNED_IN_NUM("签到次数", "signedInNum"),
        SIGNED_IN_RATE("签到次数", "signedInRate"),
        PARTICIPATE_TIME_LENGTH("签到次数", "participateTimeLength"),
        QUALIFIED_STATUS("签到次数", "qualifiedStatus");

        private final String name;
        private final String value;

        OrderFieldEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static OrderFieldEnum fromValue(String value) {
            OrderFieldEnum[] values = OrderFieldEnum.values();
            for (OrderFieldEnum orderFieldEnum : values) {
                if (Objects.equals(value, orderFieldEnum.getValue())) {
                    return orderFieldEnum;
                }
            }
            return null;
        }

    }

}
