package com.chaoxing.activity.model;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.chaoxing.activity.util.LocalDateDeserializer;
import com.chaoxing.activity.util.LocalDateSerializer;
import com.chaoxing.activity.util.LocalDateTimeDeserializer;
import com.chaoxing.activity.util.LocalDateTimeSerializer;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 活动区域标签表
 * @className: ActivityAreaFlag, table_name: t_activity_area_flag
 * @Description: 
 * @author: mybatis generator
 * @date: 2020-11-09 19:51:17
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_activity_area_flag")
public class ActivityAreaFlag {

    /** 活动id; column: activity_id*/
    private Integer activityId;
    /** 区域名称; column: area*/
    private String area;
    /** 创建时间; column: create_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime createTime;
    /** 修改时间; column: update_time*/
    @JSONField(serializeUsing = LocalDateTimeSerializer.class, deserializeUsing = LocalDateTimeDeserializer.class)
    private LocalDateTime updateTime;

}