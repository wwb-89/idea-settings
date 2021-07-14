package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

}