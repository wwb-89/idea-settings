package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.CustomAppInterfaceCall;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TCustomAppInterfaceCallMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2022-02-15 10:34:57
 * @version: ver 1.0
 */
@Mapper
public interface CustomAppInterfaceCallMapper extends BaseMapper<CustomAppInterfaceCall> {
    
    /**批量新增
     * @Description 
     * @author huxiaolong
     * @Date 2022-02-15 16:00:46
     * @param dataList
     * @return 
     */
    void batchAdd(@Param("dataList") List<CustomAppInterfaceCall> dataList);
}