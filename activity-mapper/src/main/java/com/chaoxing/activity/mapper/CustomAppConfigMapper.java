package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.CustomAppConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: CustomAppConfigMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-12-23 11:33:22
 * @version: ver 1.0
 */
@Mapper
public interface CustomAppConfigMapper extends BaseMapper<CustomAppConfig> {

    /**批量新增
     * @Description
     * @author huxiaolong
     * @Date 2021-12-23 12:58:00
     * @param dataList
     * @return
     */
    void batchAdd(List<CustomAppConfig> dataList);

    /**查询自定义应用配置模板组件的自定义应用配置列表（含有图标）
     * @Description 
     * @author huxiaolong
     * @Date 2021-12-25 14:45:51
     * @param type
     * @param templateComponentIds
     * @return
     */
    List<CustomAppConfig> listCustomAppConfigWithCloudId(@Param("type") String type, @Param("templateComponentIds") List<Integer> templateComponentIds);
}