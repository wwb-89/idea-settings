package com.chaoxing.activity.service.queue.activity.handler;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.OrgActivityDataFormPushService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgActivityDataPushQueueService
 * @description
 * @blame wwb
 * @date 2021-11-02 16:21:08
 */
@Slf4j
@Service
public class OrgActivityDataPushQueueService {

    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;
    @Resource
    private OrgActivityDataFormPushService orgActivityDataFormPushService;

    public void handle(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer fid = activity.getCreateFid();
        List<OrgDataRepoConfigDetail> orgConfigDetails = orgDataRepoConfigQueryService.listOrgConfigDetail(fid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY);
        if (CollectionUtils.isEmpty(orgConfigDetails)) {
            return;
        }
        for (OrgDataRepoConfigDetail orgConfigDetail : orgConfigDetails) {
            String repoType = orgConfigDetail.getRepoType();
            if (Objects.equals(OrgDataRepoConfigDetail.RepoTypeEnum.FORM.getValue(), repoType)) {
                // 目前只支持的仓库类型为：表单
                String repo = orgConfigDetail.getRepo();
                if (StringUtils.isBlank(repo)) {
                    return;
                }
                orgActivityDataFormPushService.push(activityId);
            }
        }
    }

}
