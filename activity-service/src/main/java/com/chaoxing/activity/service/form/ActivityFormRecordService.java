package com.chaoxing.activity.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.mapper.ActivityFormRecordMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityFormRecord;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.service.repoconfig.OrgDataRepoConfigQueryService;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

/**活动表单记录服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityFormRecordService
 * @description
 * @blame wwb
 * @date 2021-02-06 19:46:26
 */
@Slf4j
@Service
public class ActivityFormRecordService {

	@Resource
	private ActivityFormRecordMapper activityFormRecordMapper;

	@Resource
	private FormApiService formApiService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityHandleService activityHandleService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;

	@Resource
	private DistributedLock distributedLock;

	/**处理活动推送
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:35:09
	 * @param activityId
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void add(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		if (activity == null) {
			return;
		}
		String activityEditLockKey = activityHandleService.getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			ActivityFormRecord existActivityFormRecord = activityFormRecordMapper.selectOne(new QueryWrapper<ActivityFormRecord>()
					.lambda()
					.eq(ActivityFormRecord::getActivityId, activity.getId())
			);
			addOrUpdate(activity, existActivityFormRecord);
			return null;
		}, e -> {
			throw new BusinessException("处理活动推送失败");
		});
	}

	/**更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:35:23
	 * @param activityId
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void update(Integer activityId) {
		String activityEditLockKey = activityHandleService.getActivityEditLockKey(activityId);
		distributedLock.lock(activityEditLockKey, () -> {
			Activity activity = activityQueryService.getById(activityId);
			if (activity != null) {
				ActivityFormRecord existActivityFormRecord = activityFormRecordMapper.selectOne(new QueryWrapper<ActivityFormRecord>()
						.lambda()
						.eq(ActivityFormRecord::getActivityId, activity.getId())
				);
				if (existActivityFormRecord != null) {
					addOrUpdate(activity, existActivityFormRecord);
				}
			}
			return null;
		}, e -> {
			throw new BusinessException("处理活动推送失败");
		});
	}

	/**新增或更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:36:03
	 * @param activity
	 * @param existActivityFormRecord
	 * @return void
	*/
	private void addOrUpdate(Activity activity, ActivityFormRecord existActivityFormRecord) {
		if (activity == null) {
			return;
		}
		if (Objects.equals(Activity.StatusEnum.DELETED.getValue(), activity.getStatus())) {
			// 活动不存在或被删除
			if (existActivityFormRecord != null) {
				delete(activity);
			}
		} else {
			Integer createFid = activity.getCreateFid();
			Integer createUid = activity.getCreateUid();
			List<OrgDataRepoConfigDetail> orgDataRepoConfigDetails = orgDataRepoConfigQueryService.listOrgConfigDetail(createFid, OrgDataRepoConfigDetail.DataTypeEnum.ACTIVITY);
			if (CollectionUtils.isEmpty(orgDataRepoConfigDetails)) {
				return;
			}
			OrgDataRepoConfigDetail orgDataRepoConfigDetail = orgDataRepoConfigDetails.get(0);
			String repoType = orgDataRepoConfigDetail.getRepoType();
			if (Objects.equals(OrgDataRepoConfigDetail.RepoTypeEnum.FORM.getValue(), repoType)) {
				// 目前只支持表单
				String repo = orgDataRepoConfigDetail.getRepo();
				if (StringUtils.isNotBlank(repo)) {
					Integer formId = Integer.parseInt(repo);
					String formData = packageFormData(activity, formId, createFid);
					if (existActivityFormRecord == null) {
						// 新增
						Integer formUserId = formApiService.fillForm(createFid, formId, createUid, formData);
						ActivityFormRecord activityFormRecord = ActivityFormRecord.builder()
								.activityId(activity.getId())
								.formId(formId)
								.formUserId(formUserId)
								.build();
						activityFormRecordMapper.insert(activityFormRecord);
					} else {
						// 更新
						formApiService.updateForm(formId, existActivityFormRecord.getFormUserId(), formData);
					}
				}
			}
		}
	}

	/**删除
	 * @Description
	 * @author wwb
	 * @Date 2021-02-06 19:48:37
	 * @param activityId
	 * @return void
	 */
	@Transactional(rollbackFor = Exception.class)
	public void delete(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		if (activity != null) {
			delete(activity);
		}
	}

	/**删除
	 * @Description
	 * @author wwb
	 * @Date 2021-04-01 18:08:35
	 * @param activity
	 * @return void
	 */
	public void delete(Activity activity) {
		Integer activityId = activity.getId();
		ActivityFormRecord activityFormRecord = activityFormRecordMapper.selectOne(new QueryWrapper<ActivityFormRecord>()
				.lambda()
				.eq(ActivityFormRecord::getActivityId, activityId)
		);
		if (activityFormRecord == null) {
			return;
		}
		activityFormRecordMapper.delete(new QueryWrapper<ActivityFormRecord>()
				.lambda()
				.eq(ActivityFormRecord::getId, activityFormRecord.getId())
		);
		FormDTO formData = formApiService.getFormData(activity.getCreateFid(), activityFormRecord.getFormId(), activityFormRecord.getFormUserId());
		if (formData != null) {
			formApiService.deleteFormRecord(activityFormRecord.getFormId(), activityFormRecord.getFormUserId());
		}
	}

	/**根据表单行id查询活动id
	 * @Description 
	 * @author wwb
	 * @Date 2021-05-17 15:09:52
	 * @param formUserId
	 * @return java.lang.Integer
	*/
	public Integer getActivityIdByFormUserId(Integer formUserId) {
		List<ActivityFormRecord> activityFormRecords = activityFormRecordMapper.selectList(new QueryWrapper<ActivityFormRecord>()
				.lambda()
				.eq(ActivityFormRecord::getFormUserId, formUserId)
		);
		if (CollectionUtils.isNotEmpty(activityFormRecords)) {
			return activityFormRecords.get(0).getActivityId();
		}
		return null;
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