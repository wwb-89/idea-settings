package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.InspectionConfigDetail;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: InspectionConfigDetailMapper
 * @Description:
 * @author: mybatis generator
 * @date: 2021-06-16 11:01:23
 * @version: ver 1.0
 */
@Mapper
public interface InspectionConfigDetailMapper extends BaseMapper<InspectionConfigDetail> {

    /**批量新增
     * @Description 
     * @author wwb
     * @Date 2021-07-01 14:08:39
     * @param inspectionConfigDetails
     * @return int
    */
    int batchAdd(@Param("inspectionConfigDetails") List<InspectionConfigDetail> inspectionConfigDetails);

}