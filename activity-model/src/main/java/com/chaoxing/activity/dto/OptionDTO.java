package com.chaoxing.activity.dto;

import com.chaoxing.activity.model.CustomAppInterfaceCall;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**通用选项DTO，仅仅是name、value的枚举通过该类列出来
 *
 * @description:
 * @author: huxiaolong
 * @date: 2022/2/15 3:21 PM
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

    private String name;

    private String value;

    /**接口调用时机枚举列表
     * @Description 
     * @author huxiaolong
     * @Date 2022-02-15 15:24:32
     * @return
     */
    public static List<OptionDTO> listInterfaceCallTiming() {
        return Arrays.stream(CustomAppInterfaceCall.CallTimingEnum.values()).map(v -> OptionDTO.builder()
                .name(v.getName())
                .value(v.getValue())
                .build()).collect(Collectors.toList());
    }
}
