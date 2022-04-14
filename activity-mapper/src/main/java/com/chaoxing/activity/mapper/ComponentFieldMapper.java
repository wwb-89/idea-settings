package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ComponentField;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ComponentFieldMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-07-06 11:54:01
 * @version: ver 1.0
 */
@Mapper
public interface ComponentFieldMapper extends BaseMapper<ComponentField> {

    /**
     * 批量新增
     *
     * @param fieldList
     * @return void
     * @Description
     * @author huxiaolong
     * @Date 2021-07-13 16:15:51
     */
    void batchAdd(@Param("fieldList") List<ComponentField> fieldList);

    /**查询模版下的组件字段列表
     * @Description 
     * @author wwb
     * @Date 2021-12-27 17:20:11
     * @param templateId
     * @return java.util.List<com.chaoxing.activity.model.ComponentField>
    */
    List<ComponentField> listByTemplateId(@Param("templateId") Integer templateId);

}