package com.chaoxing.activity.dto.engine;

import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.model.TemplateComponent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/7 17:47 下午
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivityEngineDTO {

    private Template template;

    private List<Component> components;

    private List<TemplateComponent> templateComponents;

}
