package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 机构市场数据推送
 * @className: OrgMarketDataPush, table_name: t_org_market_data_push
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-08-02 19:06:41
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_org_market_data_push")
public class OrgMarketDataPush {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 机构id; column: fid*/
    private Integer fid;
    /** 市场id; column: market_id*/
    private Integer marketId;

}