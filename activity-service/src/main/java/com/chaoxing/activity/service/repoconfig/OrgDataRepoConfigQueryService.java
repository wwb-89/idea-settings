package com.chaoxing.activity.service.repoconfig;

import com.chaoxing.activity.mapper.OrgDataRepoConfigDetailMapper;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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
}
