package com.chaoxing.activity.service.activity.engine;

import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.dto.engine.TemplateComponentDTO;
import com.chaoxing.activity.model.*;
import com.chaoxing.activity.service.activity.component.ComponentQueryService;
import com.chaoxing.activity.service.activity.market.MarketQueryService;
import com.chaoxing.activity.service.activity.template.TemplateComponentService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

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
    @Resource
    private MarketQueryService marketQueryService;

    public ActivityEngineDTO findEngineTemplateInfo(Integer templateId, Integer marketId) {
        // 查询模板数据
        Template template = templateQueryService.getById(templateId);
        // 查询组件数据
        List<Component> components = componentQueryService.listWithOptionsByTemplateId(templateId);
        // 排序市场不需要的组件列表
        List<Integer> listExcludeComponentIds = marketQueryService.listExcludeComponentId(marketId);
        if (CollectionUtils.isNotEmpty(components)) {
            Iterator<Component> iterator = components.iterator();
            while (iterator.hasNext()) {
                Component component = iterator.next();
                if (listExcludeComponentIds.contains(component.getId())) {
                    iterator.remove();
                }
            }
        }
        // 查询模板组件关联关系
        List<TemplateComponentDTO> templateComponents = templateComponentService.listTemplateComponentInfo(templateId);
        return ActivityEngineDTO.builder()
                .template(template)
                .components(components)
                .templateComponents(templateComponents)
                .build();
    }
}
