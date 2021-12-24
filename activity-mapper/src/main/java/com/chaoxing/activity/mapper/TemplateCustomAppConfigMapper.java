package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.TemplateCustomAppConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @className: TemplateCustomAppConfigMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-12-23 11:33:22
 * @version: ver 1.0
 */
@Mapper
public interface TemplateCustomAppConfigMapper extends BaseMapper<TemplateCustomAppConfig> {

    /**批量新增
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:58:00
     * @param dataList
     * @return
     */
    void batchAdd(List<TemplateCustomAppConfig> dataList);
}