package com.chaoxing.activity.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.chaoxing.activity.model.SignUpConditionEnable;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @className: TSignUpConditionEnableMapper
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-21 18:38:35
 * @version: ver 1.0
 */
@Mapper
public interface SignUpConditionEnableMapper extends BaseMapper<SignUpConditionEnable> {
    /**新增活动报名的报名条件启用
    * @Description
    * @author huxiaolong
    * @Date 2021-07-21 19:16:47
    * @param waitSaveEnables
    * @return void
    */
    void batchAdd(@Param("waitSaveEnables") List<SignUpConditionEnable> waitSaveEnables);
}