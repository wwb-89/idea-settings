package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ClassifyShowComponent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ClassifyShowComponentMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2022-01-05 15:26:34
 * @version: ver 1.0
 */
@Mapper
public interface ClassifyShowComponentMapper extends BaseMapper<ClassifyShowComponent> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2022-01-05 15:45:32
     * @param classifyShowComponents
     * @return int
    */
    int batchAdd(@Param("classifyShowComponents") List<ClassifyShowComponent> classifyShowComponents);

}