package com.chaoxing.activity.service.volunteer;

import com.chaoxing.activity.dto.activity.VolunteerServiceDTO;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.manager.FormApprovalApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/5/19 2:42 下午
 * <p>
 */
@Slf4j
@Service
public class VolunteerService {

    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;

    @Resource
    private FormApprovalApiService formApprovalApiService;

    /**获取服务市场记录列表
     * @Description
     * @author huxiaolong
     * @Date 2021-05-19 14:10:11
     * @param uid
     * @param fid
     * @return void
     */
    public List<VolunteerServiceDTO> listServiceTimeLength(Integer uid, Integer fid) {
        List<VolunteerServiceDTO> volunteerServiceList = new ArrayList<>();
        String formType = OrgDataRepoConfigDetail.RepoTypeEnum.FORM.getValue();
        String dataType = OrgDataRepoConfigDetail.DataTypeEnum.PARTICIPATE_TIME_LENGTH.getValue();
        // 查找配置为表单类型且数据类型为参与时长的配置列表
        List<OrgDataRepoConfigDetail> configDetailList = orgDataRepoConfigQueryService.listParticipateTimeConfigDetail(fid, dataType);

        if (CollectionUtils.isNotEmpty(configDetailList)) {
            for (OrgDataRepoConfigDetail configDetail: configDetailList) {
                if (Objects.equals(formType, configDetail.getRepoType())) {
                    // 查出来的repo值应为formId
                    Integer formId = Integer.valueOf(configDetail.getRepo());

                }


            }
        }

        return volunteerServiceList;


    }
}
