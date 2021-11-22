package com.chaoxing.activity.dto.activity.create;

import lombok.*;

import javax.validation.constraints.NotNull;
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
    @NotNull(message = "机构id不能为空")
    private Integer deptId;
    /** 表单id */
    @NotNull(message = "表单id不能为空")
    private Integer formId;
    /** 表单记录id */
    @NotNull(message = "表单记录id不能为空")
    private Integer indexID;
    /** 操作用户id */
    private Integer uid;
    /** 操作方式 */
    private String op;
    /** 网页模板id */
    private Integer webTemplateId;
    /** 活动标识 */
    private String flag;

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
        DELETE("删除", "data_remove"),
        UNDEFINED("未知", "undefined");

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
            return UNDEFINED;
        }
    }
}
