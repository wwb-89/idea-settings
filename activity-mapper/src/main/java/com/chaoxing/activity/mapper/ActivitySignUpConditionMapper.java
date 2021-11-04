package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.ActivitySignUpCondition;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: ActivitySignUpConditionMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-11-02 16:57:22
 * @version: ver 1.0
 */
@Mapper
public interface ActivitySignUpConditionMapper extends BaseMapper<ActivitySignUpCondition> {

    /**
     * @Description
     * @author huxiaolong
     * @Date 2021-11-04 17:03:34
     * @param data
     * @return void
     */
    void batchAdd(@Param("data") List<ActivitySignUpCondition> data);
}