package com.chaoxing.activity.service.repoconfig;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.OrgDataRepoConfigDetailMapper;
import com.chaoxing.activity.mapper.OrgDataRepoConfigMapper;
import com.chaoxing.activity.model.OrgDataRepoConfig;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**机构数据仓库配置查询service
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:38 下午
 * <p>
 */

@Slf4j
@Service
public class OrgDataRepoConfigQueryService {

    @Resource
    private OrgDataRepoConfigMapper orgDataRepoConfigMapper;
    @Resource
    private OrgDataRepoConfigDetailMapper orgDataRepoConfigDetailMapper;

     /**根据机构fid和数据类型type查找对应的数据仓库配置详情
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:50:05
     * @param fid
     * @param dataType
     * @return java.util.List<com.chaoxing.activity.model.OrgDataRepoConfigDetail>
     */
    public List<OrgDataRepoConfigDetail> listOrgConfigDetail(Integer fid, OrgDataRepoConfigDetail.DataTypeEnum dataType) {
        return orgDataRepoConfigDetailMapper.listParticipateTimeConfigDetail(fid, dataType.getValue());
    }

     /**查找机构fid和数据类型type且仓库类型为form 的对应的数据仓库配置详情
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:50:05
     * @param fid
     * @param dataType
     * @param repoType
     * @return java.util.List<com.chaoxing.activity.model.OrgDataRepoConfigDetail>
     */
    public OrgDataRepoConfigDetail getOrgConfigDetail(Integer fid, OrgDataRepoConfigDetail.DataTypeEnum dataType, OrgDataRepoConfigDetail.RepoTypeEnum repoType) {
        List<OrgDataRepoConfigDetail> configDetailList = listOrgConfigDetail(fid, dataType);
        for (OrgDataRepoConfigDetail detail : configDetailList) {
            if (Objects.equals(repoType.getValue(), detail.getRepoType())) {
                return detail;
            }
        }
        return null;
    }

    /**根据机构fid查询机构仓库配置
     * @Description 
     * @author wwb
     * @Date 2021-06-08 17:03:22
     * @param fid
     * @return com.chaoxing.activity.model.OrgDataRepoConfig
    */
    public OrgDataRepoConfig getByFid(Integer fid) {
        List<OrgDataRepoConfig> orgDataRepoConfigs = orgDataRepoConfigMapper.selectList(new QueryWrapper<OrgDataRepoConfig>()
                .lambda()
                .eq(OrgDataRepoConfig::getFid, fid)
                .eq(OrgDataRepoConfig::getDeleted, false)
        );
        if (CollectionUtils.isNotEmpty(orgDataRepoConfigs)) {
            return orgDataRepoConfigs.get(0);
        }
        return null;
    }

    /**根据机构数据仓库配置id、仓库类型和数据类型查询配置详情
     * @Description 
     * @author wwb
     * @Date 2021-06-08 17:09:44
     * @param configId
     * @param dataType
     * @param repoType
     * @return com.chaoxing.activity.model.OrgDataRepoConfigDetail
    */
    public OrgDataRepoConfigDetail getOrgDataRepoConfigDetail(Integer configId, OrgDataRepoConfigDetail.DataTypeEnum dataType, OrgDataRepoConfigDetail.RepoTypeEnum repoType) {
        List<OrgDataRepoConfigDetail> orgDataRepoConfigDetails = orgDataRepoConfigDetailMapper.selectList(new QueryWrapper<OrgDataRepoConfigDetail>()
                .lambda()
                .eq(OrgDataRepoConfigDetail::getConfigId, configId)
                .eq(OrgDataRepoConfigDetail::getRepoType, repoType.getValue())
                .eq(OrgDataRepoConfigDetail::getDataType, dataType.getValue())
        );
        if (CollectionUtils.isNotEmpty(orgDataRepoConfigDetails)) {
            return orgDataRepoConfigDetails.get(0);
        }
        return null;
    }

}