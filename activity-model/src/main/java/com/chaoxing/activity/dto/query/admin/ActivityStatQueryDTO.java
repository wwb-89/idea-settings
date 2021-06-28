package com.chaoxing.activity.dto.query.admin;

import com.chaoxing.activity.util.enums.OrderTypeEnum;
import lombok.*;

import java.util.List;
import java.util.Objects;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/10 4:30 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityStatQueryDTO {

    private Integer fid;

    private OrderFieldEnum orderField;

    private OrderTypeEnum orderType;

    private String startDate;

    private String endDate;

    private List<Integer> activityIds;

    @Getter
    public enum OrderFieldEnum {
        
        /** 按浏览量 */
        PV("浏览量", "pv"),
        SIGNED_UP_NUM("签到人数", "signedUpNum");

        private final String name;
        private final String value;

        OrderFieldEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static OrderFieldEnum fromValue(String value) {
            OrderFieldEnum[] values = OrderFieldEnum.values();
            for (OrderFieldEnum orderFieldEnum : values) {
                if (Objects.equals(orderFieldEnum.getValue(), value)) {
                    return orderFieldEnum;
                }
            }
            return null;
        }
    }

}
