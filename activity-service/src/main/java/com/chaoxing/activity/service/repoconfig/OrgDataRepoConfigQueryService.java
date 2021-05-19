package com.chaoxing.activity.service.repoconfig;

import com.chaoxing.activity.mapper.OrgDataRepoConfigDetailMapper;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import lombok.extern.slf4j.Slf4j;
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
    private OrgDataRepoConfigDetailMapper dataRepoConfigDetailMapper;

     /**根据机构fid和数据类型type查找对应的数据仓库配置详情
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:50:05
     * @param fid
     * @param dataType
     * @return java.util.List<com.chaoxing.activity.model.OrgDataRepoConfigDetail>
     */
    public List<OrgDataRepoConfigDetail> listParticipateTimeConfigDetail(Integer fid, String dataType) {
        return dataRepoConfigDetailMapper.listParticipateTimeConfigDetail(fid, dataType);
    }

     /**查找机构fid和数据类型type且仓库类型为form 的对应的数据仓库配置详情
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:50:05
     * @param fid
     * @param dataType
     * @return java.util.List<com.chaoxing.activity.model.OrgDataRepoConfigDetail>
     */
    public OrgDataRepoConfigDetail getFormParticipateTimeConfig(Integer fid, String dataType) {
        List<OrgDataRepoConfigDetail> configDetailList = listParticipateTimeConfigDetail(fid, dataType);
        for (OrgDataRepoConfigDetail detail : configDetailList) {
            if (Objects.equals(OrgDataRepoConfigDetail.RepoTypeEnum.FORM.getValue(), detail.getRepoType())) {
                return detail;
            }
        }
        return null;
    }
}
