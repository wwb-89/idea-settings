package com.chaoxing.activity.service.activity.classify.component;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.mapper.ClassifyShowComponentMapper;
import com.chaoxing.activity.model.ClassifyShowComponent;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**分类显示组件处理服务
 * @author wwb
 * @version ver 1.0
 * @className ClassifyShowComponentHandleService
 * @description
 * @blame wwb
 * @date 2022-01-05 15:34:15
 */
@Slf4j
@Service
public class ClassifyShowComponentHandleService {

    @Resource
    private ClassifyShowComponentMapper classifyShowComponentMapper;

    @Resource
    private TemplateQueryService templateQueryService;

    /**删除模版下指定分类显示组件关联
     * @Description 当市场删除分类的时候
     * @author wwb
     * @Date 2022-01-05 15:40:28
     * @param classifyId
     * @param templateId
     * @return void
    */
    public void deleteByClassifyId(Integer classifyId, Integer templateId) {
        classifyShowComponentMapper.delete(new LambdaUpdateWrapper<ClassifyShowComponent>()
                .eq(ClassifyShowComponent::getTemplateId, templateId)
                .eq(ClassifyShowComponent::getClassifyId, classifyId)
        );
    }

    /**删除模版下分类显示组件关联
     * @Description 
     * @author wwb
     * @Date 2022-01-05 16:19:25
     * @param templateId
     * @return void
    */
    public void deleteByTemplateId(Integer templateId) {
        classifyShowComponentMapper.delete(new LambdaUpdateWrapper<ClassifyShowComponent>()
                .eq(ClassifyShowComponent::getTemplateId, templateId)
        );
    }

    /**关联
     * @Description 
     * @author wwb
     * @Date 2022-01-05 15:44:01
     * @param classifyId
     * @param templateId
     * @param templateComponentIds
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void associated(Integer classifyId, Integer templateId, List<Integer> templateComponentIds) {
        // 先删除
        deleteByClassifyId(classifyId, templateId);
        Template template = templateQueryService.getById(templateId);
        Integer marketId = template.getMarketId();
        if (CollectionUtils.isNotEmpty(templateComponentIds)) {
            List<ClassifyShowComponent> classifyShowComponents = Lists.newArrayList();
            templateComponentIds.forEach(v -> classifyShowComponents.add(ClassifyShowComponent.builder()
                    .marketId(marketId)
                    .templateId(templateId)
                    .classifyId(classifyId)
                    .templateComponentId(v)
                    .build()));
            classifyShowComponentMapper.batchAdd(classifyShowComponents);
        }
    }

    /**关联
     * @Description 
     * @author wwb
     * @Date 2022-01-05 16:20:44
     * @param classifyShowComponents
     * @param templateId
     * @return void
    */
    public void associated(List<ClassifyShowComponent> classifyShowComponents, Integer templateId) {
        // 先删除
        deleteByTemplateId(templateId);
        Template template = templateQueryService.getById(templateId);
        Integer marketId = template.getMarketId();
        if (CollectionUtils.isNotEmpty(classifyShowComponents)) {
            classifyShowComponents.forEach(v -> {
                v.setTemplateId(templateId);
                v.setMarketId(marketId);
            });
            classifyShowComponentMapper.batchAdd(classifyShowComponents);
        }
    }

    /**更新关联
     * @Description 当分类修改后（id改变后）
     * @author wwb
     * @Date 2022-01-05 15:53:29
     * @param oldClassifyId
     * @param newClassifyId
     * @param templateId
     * @return void
    */
    public void updateClassifyAssociated(Integer oldClassifyId, Integer newClassifyId, Integer templateId) {
        classifyShowComponentMapper.update(null, new LambdaUpdateWrapper<ClassifyShowComponent>()
                .eq(ClassifyShowComponent::getTemplateId, templateId)
                .eq(ClassifyShowComponent::getClassifyId, oldClassifyId)
                .set(ClassifyShowComponent::getClassifyId, newClassifyId)
        );
    }

}