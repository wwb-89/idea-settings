package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.TemplateSignUpCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TemplateSignUpConditionMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-11-02 16:56:56
 * @version: ver 1.0
 */
@Mapper
public interface TemplateSignUpConditionMapper extends BaseMapper<TemplateSignUpCondition> {


    /**
     * 批量保存模板报名条件明细
     *
     * @param data
     * @return void
     * @Description
     * @author huxiaolong
     * @Date 2021-11-04 10:43:36
     */
    void batchAdd(@Param("data") List<TemplateSignUpCondition> data);

    /**根据模版组件id查询报名条件明细
     * @Description 
     * @author wwb
     * @Date 2021-11-29 16:02:05
     * @param templateComponentId
     * @return void
    */
    void deleteByTemplateComponentId(@Param("templateComponentId") Integer templateComponentId);

}