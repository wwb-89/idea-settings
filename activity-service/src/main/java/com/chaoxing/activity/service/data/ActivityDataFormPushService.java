package com.chaoxing.activity.service.data;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushRecord;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**活动数据表单推送服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityDataFormPushService
 * @description 将活动数据推送到表单
 * @blame wwb
 * @date 2021-06-24 19:11:02
 */
@Slf4j
@Service
public class ActivityDataFormPushService {

    @Resource
    private FormApiService formApiService;
    @Resource
    private ActivityQueryService activityQueryService;
    @Resource
    private SignApiService signApiService;

    @Resource
    private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;
    @Resource
    private DataPushRecordQueryService dataPushRecordQueryService;
    @Resource
    private DataPushRecordHandleService dataPushRecordHandleService;

    /**推送数据
     * @Description 
     * @author wwb
     * @Date 2021-06-24 20:25:11
     * @param activityId
     * @return void
    */
    @Transactional(rollbackFor = Exception.class)
    public void push(Integer activityId) {
        Activity activity = activityQueryService.getById(activityId);
        if (activity == null) {
            return;
        }
        Integer createFid = activity.getCreateFid();
        OrgDataRepoConfigDetail.RepoTypeEnum repoType = OrgDataRepoConfigDetail.RepoTypeEnum.FORM;
        OrgDataRepoConfigDetail.DataTypeEnum dataType = OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY;
        OrgDataRepoConfigDetail orgConfigDetail = orgDataRepoConfigQueryService.getOrgConfigDetail(createFid, dataType, repoType);
        if (orgConfigDetail == null) {
            return;
        }
        String repo = orgConfigDetail.getRepo();
        if (StringUtils.isBlank(repo)) {
            return;
        }
        String identify = String.valueOf(activityId);
        DataPushRecord dataPushRecord = dataPushRecordQueryService.get(identify, repoType.getValue(), dataType.getValue());
        Integer status = activity.getStatus();
        Integer formId = Integer.parseInt(repo);
        // 是否需要删除数据
        boolean delete = false;
        if (Objects.equals(Activity.StatusEnum.DELETED.getValue(), status) || !activity.getReleased()) {
            delete = true;
        }
        if (delete) {
            if (dataPushRecord != null) {
                dataPushRecordHandleService.delete(dataPushRecord.getId());
                Integer formUserId = Integer.parseInt(dataPushRecord.getRecord());
                // 删除记录
                FormDTO formData = formApiService.getFormData(activity.getCreateFid(), formId, formUserId);
                if (formData != null) {
                    formApiService.deleteFormRecord(formId, formUserId);
                }
            }
        } else {
            Integer createUid = activity.getCreateUid();
            FormDTO existFormData = null;
            if (dataPushRecord != null) {
                Integer formUserId = Integer.parseInt(dataPushRecord.getRecord());
                existFormData = formApiService.getFormData(activity.getCreateFid(), formId, formUserId);
            }
            String formData = packageFormData(activity, formId, createFid);
            if (existFormData == null) {
                // 新增
                Integer formUserId = formApiService.fillForm(createFid, formId, createUid, formData);
                String record = String.valueOf(formUserId);
                if (dataPushRecord == null) {
                    dataPushRecord = DataPushRecord.builder()
                            .identify(String.valueOf(activityId))
                            .dataType(dataType.getValue())
                            .repoType(repoType.getValue())
                            .repo(repo)
                            .record(record)
                            .build();
                    dataPushRecordHandleService.add(dataPushRecord);
                } else {
                    dataPushRecordHandleService.update(dataPushRecord.getId(), record);
                }
            } else {
                // 更新
                Integer formUserId = Integer.parseInt(dataPushRecord.getRecord());
                formApiService.updateForm(formId, formUserId, formData);
            }
        }
    }

    private String packageFormData(Activity activity, Integer formId, Integer createFid) {
        List<FormStructureDTO> formInfo = formApiService.getFormInfo(createFid, formId);
        JSONArray result = new JSONArray();
        for (FormStructureDTO formStructure : formInfo) {
            String alias = formStructure.getAlias();
            JSONObject item = new JSONObject();
            item.put("compt", formStructure.getCompt());
            item.put("comptId", formStructure.getId());
            JSONArray data = new JSONArray();
            result.add(item);
            // 活动id
            if ("activity_id".equals(alias)) {
                data.add(activity.getId());
                item.put("val", data);
                continue;
            }
            // 活动名称
            if ("activity_name".equals(alias)) {
                data.add(activity.getName());
                item.put("val", data);
                continue;
            }
            // 报名参与范围
            if ("sign_up_participate_scope".equals(alias)) {
                data.add(signApiService.getActivitySignParticipateScopeDescribe(activity.getSignId()));
                item.put("val", data);
                continue;
            }
            // 创建单位
            if ("create_org".equals(alias)) {
                data.add(activity.getCreateOrgName());
                item.put("val", data);
                continue;
            }
            // 活动分类
            if ("activity_classify".equals(alias)) {
                data.add(activity.getActivityClassifyName());
                item.put("val", data);
                continue;
            }
            // 活动积分
            if ("activity_integral".equals(alias)) {
                data.add(activity.getIntegralValue());
                item.put("val", data);
                continue;
            }
            // 单位
            if ("unit".equals(alias)) {
                data.add("积分");
                item.put("val", data);
                continue;
            }
            // 活动预览
            if ("preview_url".equals(alias)) {
                data.add(activity.getPreviewUrl());
                item.put("val", data);
                continue;
            }
            // 发起人
            if ("create_user".equals(alias)) {
                JSONObject user = new JSONObject();
                user.put("id", activity.getCreateUid());
                user.put("name", activity.getCreateUserName());
                data.add(user);
                item.put("idNames", data);
                continue;
            }
            // 活动状态
            if ("activity_status".equals(alias)) {
                Integer status = activity.getStatus();
                Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
                data.add(statusEnum.getName());
                item.put("val", data);
            }
        }
        return result.toJSONString();
    }

}