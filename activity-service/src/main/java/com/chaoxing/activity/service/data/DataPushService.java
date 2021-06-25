package com.chaoxing.activity.service.data;

import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.queue.DataPushQueueService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**数据推送服务
 * @author wwb
 * @version ver 1.0
 * @className DataPushService
 * @description 所有涉及到三方数据推送，根据配置信息
 * 1、活动发布、修改需要推送到表单的活动记录表中
 * 2、用户产生的活动数据需要推送到表单中
 * @blame wwb
 * @date 2021-06-24 18:35:05
 */
@Slf4j
@Service
public class DataPushService {

    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;
    @Resource
    private ActivityDataFormPushService activityDataFormPushService;
    @Resource
    private UserActivityDataFormPushService userActivityDataFormPushService;
    @Resource
    private DataPushQueueService dataPushQueueService;

    /**数据推送
     * @Description 
     * @author wwb
     * @Date 2021-06-24 19:36:21
     * @param dataPushParam
     * @return void
    */
    public void dataPush(DataPushParamDTO dataPushParam) {
        dataPushQueueService.push(dataPushParam);
    }

    /**处理数据推送
     * @Description 
     * @author wwb
     * @Date 2021-06-24 19:54:15
     * @param dataPushParam
     * @return void
    */
    public void handleDataPush(DataPushParamDTO dataPushParam) {
        Integer fid = dataPushParam.getFid();
        OrgDataRepoConfigDetail.DataTypeEnum dataType = dataPushParam.getDataType();
        List<OrgDataRepoConfigDetail> orgConfigDetails = orgDataRepoConfigQueryService.listOrgConfigDetail(fid, dataType);
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
                // 目前只支持活动信息推送、用户行为数据推送
                String identify = dataPushParam.getIdentify();
                switch (dataType) {
                    case ACTIVITY:
                        activityDataFormPushService.push(Integer.parseInt(identify));
                        break;
                    case USER_ACTIVITY_DATA:
                        // TODO 后面迁移
                        break;
                    default:
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class DataPushParamDTO {

        /** 机构id */
        private Integer fid;
        /** 数据类型 */
        private OrgDataRepoConfigDetail.DataTypeEnum dataType;
        /** 主键标识 */
        private String identify;
        /** 附加附件标识 */
        private String additionalIdentify;
    }

}