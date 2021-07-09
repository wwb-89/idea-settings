package com.chaoxing.activity.service.activity.engine;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.engine.ActivityEngineDTO;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.mapper.TemplateComponentMapper;
import com.chaoxing.activity.mapper.TemplateMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.model.TemplateComponent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    @Autowired
    private TemplateMapper templateMapper;
    @Autowired
    private TemplateComponentMapper templateComponentMapper;
    @Autowired
    private ComponentMapper componentMapper;
    @Autowired
    private ComponentFieldMapper componentFieldMapper;


    public ActivityEngineDTO findEngineTemplateInfo(Integer fid, Integer templateId) {
        // 查询模板数据
        Template template = templateMapper.selectById(templateId);
        // 查询组件数据
        List<Component> components = listComponentByFid(fid);
        // 查询模板组件关联关系
        List<TemplateComponent> templateComponents = listTemplateComponentByTemplateId(templateId);

        return ActivityEngineDTO.builder()
                .template(template)
                .components(components)
                .templateComponents(templateComponents)
                .build();
    }


    /**根据机构fid，查询除系统模板外，其他模板
    * @Description
    * @author huxiaolong
    * @Date 2021-07-06 14:35:58
    * @param fid
    * @return java.util.List<com.chaoxing.activity.model.Template>
    */
    public List<Template> listTemplateByFid(Integer fid) {
        return templateMapper.selectList(new QueryWrapper<Template>()
                .lambda()
                .eq(Template::getSystem, Boolean.TRUE)
                .or()
                .eq(Template::getFid, fid)
                .orderByAsc(Template::getSequence));
    }

    /**系统组件 + fid 的自定义组件 = 组件集合
    * @Description
    * @author huxiaolong
    * @Date 2021-07-07 15:31:05
    * @param fid
    * @return void
    */
    public List<Component> listComponentByFid(Integer fid) {
        // 系统组件(isSystem: true, fid: null) + fid 自身的组件
        List<Component> components = componentMapper.selectList(new QueryWrapper<Component>()
                .lambda()
                .eq(Component::getFid, fid)
                .or(j -> j.eq(Component::getSystem, Boolean.TRUE)
                        .isNull(Component::getFid)));


        for (Component component : components) {
            if (!component.getSystem() && StringUtils.isNotBlank(component.getType())) {
                // todo 自定义组件处理
            }
        }
        return components;
    }

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-07 15:30:53
    * @param templateId
    * @return void
    */
    public List<TemplateComponent> listTemplateComponentByTemplateId(Integer templateId) {
        return templateComponentMapper.selectList(new QueryWrapper<TemplateComponent>()
                .lambda()
                .eq(TemplateComponent::getTemplateId, templateId)
                .orderByAsc(TemplateComponent::getSequence));
    }
}
