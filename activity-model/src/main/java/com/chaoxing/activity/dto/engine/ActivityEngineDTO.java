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

    /** 模板信息 */
    private Template template;

    /** 组件信息list */
    private List<Component> components;

    /** 待更新templateId 自定义组件ids */
    private List<Integer> customComponentIds;

    /** 待删除templateComponentId 自定义组件ids */
    private List<Integer> delTemplateComponentIds;

    /** 待删除自定义组件id 自定义组件ids */
    private List<Integer> delCustomComponentIds;

    /** 模板组件关联信息list */
    private List<TemplateComponent> templateComponents;

    /** 模板组件关联信息list */
    private List<TemplateComponentDTO> showTemplateComponents;

}
