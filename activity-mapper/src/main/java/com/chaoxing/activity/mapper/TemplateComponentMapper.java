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

    /**
    * @Description
    * @author huxiaolong
    * @Date 2021-07-08 11:54:17
    * @param templateComponents
    * @return void
    */
    void batchAdd(@Param("templateComponents") Collection<TemplateComponent> templateComponents);

    /**
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
}