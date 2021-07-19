package com.chaoxing.activity.dto.manager.wfwform;

import com.chaoxing.activity.model.Classify;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**表单过滤用到的text:value的键值对实体
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/31 3:44 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwFormFilterDTO {

    private String text;

    private String value;

    public static WfwFormFilterDTO buildFromClassify(Classify classify) {
        return WfwFormFilterDTO.builder()
                .text(classify.getName())
                .value(String.valueOf(classify.getId()))
                .build();
    }

    public static List<WfwFormFilterDTO> buildFromClassifies(List<Classify> classifies) {
        return Optional.ofNullable(classifies).orElse(Lists.newArrayList()).stream().map(WfwFormFilterDTO::buildFromClassify).collect(Collectors.toList());
    }

}
