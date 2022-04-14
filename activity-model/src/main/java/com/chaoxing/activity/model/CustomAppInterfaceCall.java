package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.google.common.collect.Lists;
import lombok.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 自定义应用接口调用
 * @className: CustomAppInterfaceCall, table_name: t_custom_app_interface_call
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-02-15 10:34:57
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_custom_app_interface_call")
public class CustomAppInterfaceCall {

    /** 组件; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 自定义应用组件id; column: component_id*/
    private Integer componentId;
    /** 自定义应用模版组件id; column: template_component_id*/
    private Integer templateComponentId;
    /** 接口地址; column: url*/
    private String url;
    /** 是否创建时调用; column: is_create_call*/
    @TableField(value = "is_create_call")
    private Boolean createCall;
    /** 是否发布时调用; column: is_release_call*/
    @TableField(value = "is_release_call")
    private Boolean releaseCall;
    /** 是否下架时调用; column: is_cancel_release_call*/
    @TableField(value = "is_cancel_release_call")
    private Boolean cancelReleaseCall;
    /** 是否开始时调用; column: is_start_call*/
    @TableField(value = "is_start_call")
    private Boolean startCall;
    /** 是否结束时调用; column: is_end_call*/
    @TableField(value = "is_end_call")
    private Boolean endCall;
    /** 是否删除时调用; column: is_delete_call*/
    @TableField(value = "is_delete_call")
    private Boolean deleteCall;

    @TableField(exist = false)
    private List<String> callTimings;

    /** 调用时机枚举 */
    @Getter
    public enum CallTimingEnum {

        /** 创建调用 */
        CREATE_CALL("创建时", "create_call"),
        /** 发布调用 */
        RELEASE_CALL("发布时", "release_call"),
        /** 下架调用 */
        CANCEL_RELEASE_CALL("下架时", "cancel_release_call"),
        /** 开始调用 */
        START_CALL("开始时", "start_call"),
        /** 结束调用 */
        END_CALL("结束时", "end_call"),
        /** 删除调用 */
        DELETE_CALL("删除时", "delete_call");

        private final String name;
        private final String value;

        CallTimingEnum(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public static CallTimingEnum fromValue(String value) {
            CallTimingEnum[] values = CallTimingEnum.values();
            for (CallTimingEnum callTimingEnum : values) {
                if (Objects.equals(callTimingEnum.getValue(), value)) {
                    return callTimingEnum;
                }
            }
            return null;
        }
    }

    /**调用时机列表转换状态属性值
     * @Description 
     * @author huxiaolong
     * @Date 2022-02-15 14:47:19
     * @return
     */
    public void callTimingTransfer2StatusVal() {
        List<String> callTimings = Optional.ofNullable(getCallTimings()).orElse(Lists.newArrayList());
        this.createCall = callTimings.contains(CallTimingEnum.CREATE_CALL.getValue());
        this.releaseCall = callTimings.contains(CallTimingEnum.RELEASE_CALL.getValue());
        this.cancelReleaseCall = callTimings.contains(CallTimingEnum.CANCEL_RELEASE_CALL.getValue());
        this.startCall = callTimings.contains(CallTimingEnum.START_CALL.getValue());
        this.endCall = callTimings.contains(CallTimingEnum.END_CALL.getValue());
        this.deleteCall = callTimings.contains(CallTimingEnum.DELETE_CALL.getValue());
    }

    /**状态属性值转换成调用时机列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-02-15 16:29:10
     * @return
     */
    public void statusValTransfer2CallTiming() {
        this.callTimings = Lists.newArrayList();
        if (Optional.ofNullable(this.createCall).orElse(false)) {
            this.callTimings.add(CallTimingEnum.CREATE_CALL.getValue());
        }
        if (Optional.ofNullable(this.releaseCall).orElse(false)) {
            this.callTimings.add(CallTimingEnum.RELEASE_CALL.getValue());
        }
        if (Optional.ofNullable(this.cancelReleaseCall).orElse(false)) {
            this.callTimings.add(CallTimingEnum.CANCEL_RELEASE_CALL.getValue());
        }
        if (Optional.ofNullable(this.startCall).orElse(false)) {
            this.callTimings.add(CallTimingEnum.START_CALL.getValue());
        }
        if (Optional.ofNullable(this.endCall).orElse(false)) {
            this.callTimings.add(CallTimingEnum.END_CALL.getValue());
        }
        if (Optional.ofNullable(this.deleteCall).orElse(false)) {
            this.callTimings.add(CallTimingEnum.DELETE_CALL.getValue());
        }
    }

}