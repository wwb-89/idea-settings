package com.chaoxing.activity.service.data;

import com.chaoxing.activity.model.OrgDataRepoConfig;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**数据推送验证服务
 * @author wwb
 * @version ver 1.0
 * @className DataPushValidationService
 * @description
 * @blame wwb
 * @date 2021-08-03 14:30:28
 */
@Slf4j
@Service
public class DataPushValidationService {

    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;

    /**数据是否需要推送
     * @Description
     * @author wwb
     * @Date 2021-08-03 14:07:05
     * @param fid
     * @param marketId
     * @return boolean
     */
    public boolean pushAble(Integer fid, Integer marketId) {
        OrgDataRepoConfig orgDataRepoConfig = orgDataRepoConfigQueryService.getByFid(fid);
        Boolean specifyMarket = Optional.ofNullable(orgDataRepoConfig).map(OrgDataRepoConfig::getSpecifyMarket).orElse(false);
        if (!specifyMarket) {
            return true;
        }
        if (marketId == null) {
            return false;
        }
        // 查询配置的市场id列表
        List<Integer> marketIds = orgDataRepoConfigQueryService.listMarketIdByFid(fid);
        return marketIds.contains(marketId);
    }

}
