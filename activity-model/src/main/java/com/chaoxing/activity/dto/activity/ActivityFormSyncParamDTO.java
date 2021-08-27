package com.chaoxing.activity.dto.activity;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.util.Objects;

/**活动表单同步参数
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/25 14:22
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityFormSyncParamDTO {

    private Integer fid;

    private Integer formId;

    private Integer formUserId;

    private String operateType;

    private Integer webTemplateId;

    /**同步操作类型
    * @Description 
    * @author huxiaolong
    * @Date 2021-08-26 16:49:55
    * @return
    */
    @Getter
    public enum OperateTypeEnum {

        /** 创建活动 */
        CREATE("创建", "create"),
        UPDATE("更新", "update"),
        DELETE("删除", "delete");

        private final String name;
        private final String value;

        OperateTypeEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static OperateTypeEnum fromValue(String value) {
            OperateTypeEnum[] values = OperateTypeEnum.values();
            for (OperateTypeEnum operateTypeEnum : values) {
                if (Objects.equals(operateTypeEnum.getValue(), value)) {
                    return operateTypeEnum;
                }
            }
            throw new BusinessException("未知同步操作类型");
        }
    }
}
