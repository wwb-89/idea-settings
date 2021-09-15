package com.chaoxing.activity.dto.activity.create;

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
public class ActivityCreateFromFormParamDTO {

    /** 机构单位id */
    private Integer deptId;
    /** 表单id */
    private Integer formId;
    /** 表单记录id */
    private Integer indexID;
    /** 操作用户id */
    private Integer uid;
    /** 操作方式 */
    private String op;
    /** 网页模板id */
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
        CREATE("创建", "data_create"),
        UPDATE("更新", "data_update"),
        DELETE("删除", "data_remove");

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
