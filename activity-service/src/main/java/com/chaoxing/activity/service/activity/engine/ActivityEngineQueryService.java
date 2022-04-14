package com.chaoxing.activity.service.activity.engine;

import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.dto.engine.TemplateComponentDTO;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.component.ComponentQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/7/6 2:29 下午
 * <p>
 */
@Slf4j
@Service
public class ActivityEngineQueryService {

    @Resource
    private TemplateQueryService templateQueryService;
    @Resource
    private ComponentQueryService componentQueryService;
    @Resource
    private TemplateComponentService templateComponentService;

    /**根据模板id和市场id查询模板数据(模板字段设置页面使用该查询)
     * @Description
     * @author huxiaolong
     * @Date 2021-11-03 16:54:18
     * @param templateId
     * @param marketId
     * @return com.chaoxing.activity.dto.engine.ActivityEngineDTO
     */
    public ActivityEngineDTO findEngineTemplateInfo(Integer templateId, Integer marketId) {
        // 查询模板数据
        Template template = templateQueryService.getById(templateId);
        // 查询组件数据
        List<Component> components = componentQueryService.listWithOptionsByTemplateId(templateId);
        // 查询模板组件关联关系
        List<TemplateComponentDTO> templateComponents = templateComponentService.listTemplateComponentInfo(templateId);
        return ActivityEngineDTO.builder()
                .template(template)
                .components(components)
                .templateComponents(templateComponents)
                .build();
    }
}
