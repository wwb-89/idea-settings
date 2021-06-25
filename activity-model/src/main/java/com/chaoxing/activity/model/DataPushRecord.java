package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**数据推送记录表
 * @className: DataPushRecord, table_name: t_data_push_record
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-06-11 10:49:22
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_data_push_record")
public class DataPushRecord {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Long id;
    /** 主键标识; column: identify*/
    private String identify;
    /** 数据类型; column: data_type*/
    private String dataType;
    /** 仓库类型; column: repo_type*/
    private String repoType;
    /** 仓库; column: repo*/
    private String repo;
    /** 推送记录标识; column: record*/
    private String record;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}