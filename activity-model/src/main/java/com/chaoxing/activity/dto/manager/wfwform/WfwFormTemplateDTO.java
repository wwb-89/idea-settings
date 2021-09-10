package com.chaoxing.activity.dto.manager.wfwform;

import com.chaoxing.activity.model.SignUpFillInfoType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @desc 万能表单模板数据
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/8/17 11:14
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormTemplateDTO {

    private String name;

    private String value;

    private String sign;

    private String key;

    public static WfwFormTemplateDTO buildWfwFormTemplateEnum(SignUpFillInfoType.WfwFormTemplateEnum wfwFormTemplateEnum) {
        return WfwFormTemplateDTO.builder()
                .name(wfwFormTemplateEnum.getName())
                .value(wfwFormTemplateEnum.getValue())
                .sign(wfwFormTemplateEnum.getSign())
                .key(wfwFormTemplateEnum.getKey())
                .build();
    }

    public static List<WfwFormTemplateDTO> wfwFormTemplateList() {
        return Arrays.stream(SignUpFillInfoType.WfwFormTemplateEnum.values())
                .map(WfwFormTemplateDTO::buildWfwFormTemplateEnum)
                .collect(Collectors.toList());
    }

}
