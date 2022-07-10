package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.dto.engine.TemplateComponentDTO;
import com.chaoxing.activity.model.TemplateComponent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * @className: TemplateComponentMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-06 11:53:54
 * @version: ver 1.0
 */
@Mapper
public interface TemplateComponentMapper extends BaseMapper<TemplateComponent> {

    /**批量新增
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 11:54:17
    * @param templateComponents
    * @return void
    */
    void batchAdd(@Param("templateComponents") Collection<TemplateComponent> templateComponents);

    /**查询模版下的模版组件列表
    * @Description
    * @author huxiaolong
    * @Date 2021-07-15 14:43:01
    * @param templateId
    * @return java.util.List<com.chaoxing.activity.dto.engine.TemplateComponentDTO>
    */
    List<TemplateComponentDTO> listTemplateComponentInfo(Integer templateId);

    /**查询模板中报名组件数量
    * @Description 
    * @author huxiaolong
    * @Date 2021-09-02 19:42:52
    * @param templateId
    * @return int
    */
    int countTemplateSignUp(@Param("templateId") Integer templateId);

    /**根据模板id和组件code查询模板是否关联组件
     * @Description
     * @author huxiaolong
     * @Date 2021-10-27 10:30:22
     * @param templateId
     * @param componentCode
     * @return int
     */
    int countTemplateComponentByCode(@Param("templateId") Integer templateId, @Param("componentCode") String componentCode);

    /**根据模板id和组件code查询模板组件列表
     * @Description
     * @author huxiaolong
     * @Date 2021-11-03 18:05:47
     * @param templateId
     * @param componentCode
     * @return java.util.List<com.chaoxing.activity.model.TemplateComponent>
     */
    List<TemplateComponent> listTemplateComponentByCode(@Param("templateId") Integer templateId, @Param("componentCode") String componentCode);
}