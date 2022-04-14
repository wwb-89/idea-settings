package com.chaoxing.activity.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 活动市场与活动分类关联表
 * @className: TMarketClassify, table_name: t_market_classify
 * @Description: 
 * @author: mybatis generator
 * @date: 2021-07-19 15:03:35
 * @version: ver 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "t_market_classify")
public class MarketClassify {

    /** 主键; column: id*/
    @TableId(type = IdType.AUTO)
    private Integer id;
    /** 活动市场id; column: market_id*/
    private Integer marketId;
    /** 分类id; column: classify_id*/
    private Integer classifyId;
    /** 顺序; column: sequence*/
    private Integer sequence;
    /** 创建时间; column: create_time*/
    private LocalDateTime createTime;
    /** 更新时间; column: update_time*/
    private LocalDateTime updateTime;

    public static MarketClassify buildFromClassify(Classify classify, Integer marketId, Integer sequence) {
        return MarketClassify.builder()
                .marketId(marketId)
                .classifyId(classify.getId())
                .sequence(sequence)
                .build();
    }

    public static List<MarketClassify> buildFromClassifies(List<Classify> classifies, Integer marketId) {
        return classifies.stream().map(v -> MarketClassify.buildFromClassify(v, marketId, 1)).collect(Collectors.toList());
    }

    public static void handleSequence(List<MarketClassify> marketClassifies, Integer initialSequence) {
        for (MarketClassify marketClassify : marketClassifies) {
            marketClassify.setSequence(initialSequence++);
        }
    }

}