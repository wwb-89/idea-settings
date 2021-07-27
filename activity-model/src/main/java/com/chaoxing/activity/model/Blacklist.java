package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 黑名单表
 * @className: Blacklist, table_name: t_blacklist
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-26 18:45:24
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_blacklist")
public class Blacklist {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 市场id; column: market_id*/
    private Integer marketId;
    /** 用户id; column: uid*/
    private Integer uid;
    /** 加入黑名单时间; column: join_time*/
    private LocalDateTime joinTime;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

}