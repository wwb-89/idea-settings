package com.chaoxing.activity.service.activity.classify.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ClassifyShowComponentMapper;
import com.chaoxing.activity.model.ClassifyShowComponent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**分类显示组件查询服务
 * @author wwb
 * @version ver 1.0
 * @className ClassifyShowComponentQueryService
 * @description
 * @blame wwb
 * @date 2022-01-05 15:34:05
 */
@Slf4j
@Service
public class ClassifyShowComponentQueryService {

    @Resource
    private ClassifyShowComponentMapper classifyShowComponentMapper;

    /**根据模版id查询配置了显示关联的数据
     * @Description 
     * @author wwb
     * @Date 2022-01-05 15:38:12
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.model.ClassifyShowComponent>
    */
    public List<ClassifyShowComponent> listByTemplateId(Integer templateId) {
        return classifyShowComponentMapper.selectList(new LambdaQueryWrapper<ClassifyShowComponent>()
                .eq(ClassifyShowComponent::getTemplateId, templateId)
        );
    }

    /**根据模版id查询关联的显示模版组件id集合
     * @Description 
     * @author wwb
     * @Date 2022-01-05 15:58:27
     * @param templateId
     * @return java.util.Set<java.lang.Integer>
    */
    public Set<Integer> listTemplateComponentIdByTemplateId(Integer templateId) {
        List<ClassifyShowComponent> classifyShowComponents = classifyShowComponentMapper.selectList(new LambdaQueryWrapper<ClassifyShowComponent>()
                .eq(ClassifyShowComponent::getTemplateId, templateId)
        );
        return classifyShowComponents.stream().map(ClassifyShowComponent::getTemplateComponentId).collect(Collectors.toSet());
    }

}