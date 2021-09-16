package com.chaoxing.activity.dto.manager.mh;

import com.chaoxing.activity.model.Market;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/9/16 11:44
 * <p>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MhMarketDataCenterDTO {
    /** 市场id */
    private Integer id;
    /** 市场名称 */
    private String name;
    /** 活动数据url */
    private String divUrl;
    /** 分类查询Url */
    private String searchClassifyUrl;
    /** 活动广场url */
    private String moreUrl;



    public static MhMarketDataCenterDTO buildFromMarket(Market market) {
        Integer marketId = market.getId();
        return MhMarketDataCenterDTO.builder()
                .id(marketId)
                .name(market.getName())
                .divUrl(buildDivUrl(marketId))
                .searchClassifyUrl(buildClassifyUrl(marketId))
                .moreUrl(buildMoreUrl(marketId))
                .build();
    }

    private static String buildDivUrl(Integer marketId) {
        return UrlConstant.API_DOMAIN + "/mh/data-center/market/" + marketId;
    }

    private static String buildClassifyUrl(Integer marketId) {
        return UrlConstant.API_DOMAIN + "/mh/data-center/market/" + marketId + "/classifies";
    }

    private static String buildMoreUrl(Integer marketId) {
        return UrlConstant.DOMAIN + "?marketId=" + marketId;
    }


}
