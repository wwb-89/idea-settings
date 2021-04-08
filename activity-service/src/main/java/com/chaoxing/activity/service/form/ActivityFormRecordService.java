package com.chaoxing.activity.service.form;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.manager.form.FormDTO;
import com.chaoxing.activity.dto.manager.form.FormStructureDTO;
import com.chaoxing.activity.mapper.ActivityFormRecordMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityFormRecord;
import com.chaoxing.activity.model.OrgForm;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.util.DistributedLock;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

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
	private OrgFormService orgFormService;
	@Resource
	private FormApiService formApiService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private ActivityHandleService activityHandleService;

	@Resource
	private DistributedLock distributedLock;

	/**处理活动推送
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 17:35:09
	 * @param activityId
	 * @return void
	*/
	public void add(Integer activityId) {
		String activityEditLockKey = activityHandleService.getActivityEditLockKey(activityId);
		Consumer<Exception> fail = (e) -> {
			throw new BusinessException("处理活动推送失败");
		};
		distributedLock.lock(activityEditLockKey, () -> {
			Activity activity = activityQueryService.getById(activityId);
			ActivityFormRecord existActivityFormRecord = activityFormRecordMapper.selectOne(new QueryWrapper<ActivityFormRecord>()
					.lambda()
					.eq(ActivityFormRecord::getActivityId, activity.getId())
			);
			addOrUpdate(activity, existActivityFormRecord);
			return null;
		}, fail);
	}

	/**更新
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-26 19:35:23
	 * @param activityId
	 * @return void
	*/
	public void update(Integer activityId) {
		String activityEditLockKey = activityHandleService.getActivityEditLockKey(activityId);
		Consumer<Exception> fail = (e) -> {
			throw new BusinessException("处理活动推送失败");
		};
		distributedLock.lock(activityEditLockKey, () -> {
			Activity activity = activityQueryService.getById(activityId);
			ActivityFormRecord existActivityFormRecord = activityFormRecordMapper.selectOne(new QueryWrapper<ActivityFormRecord>()
					.lambda()
					.eq(ActivityFormRecord::getActivityId, activity.getId())
			);
			if (existActivityFormRecord != null) {
				addOrUpdate(activity, existActivityFormRecord);
			}
			return null;
		}, fail);
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
		if (activity == null || Objects.equals(Activity.StatusEnum.DELETED.getValue(), activity.getStatus())) {
			// 活动不存在或被删除
			if (existActivityFormRecord != null) {
				delete(activity);
			}
		} else {
			Integer createFid = activity.getCreateFid();
			Integer createUid = activity.getCreateUid();
			OrgForm orgForm = orgFormService.getByFid(activity.getCreateFid());
			if (orgForm == null) {
				return;
			}
			Integer formId = orgForm.getFormId();
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

	/**删除
	 * @Description
	 * @author wwb
	 * @Date 2021-02-06 19:48:37
	 * @param activityId
	 * @return void
	 */
	public void delete(Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		delete(activity);
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
		FormDTO formData = formApiService.getFormData(activity.getCreateFid(), activityFormRecord.getFormId(), activityFormRecord.getFormUserId());
		if (formData != null) {
			formApiService.deleteFormRecord(activityFormRecord.getFormId(), activityFormRecord.getFormUserId());
		}
		activityFormRecordMapper.delete(new QueryWrapper<ActivityFormRecord>()
				.lambda()
				.eq(ActivityFormRecord::getId, activityFormRecord.getActivityId())
		);
	}

	private String packageFormData(Activity activity, Integer formId, Integer createFid) {
		List<FormStructureDTO> formInfo = formApiService.getFormInfo(createFid, formId);
		JSONArray result = new JSONArray();
		for (FormStructureDTO formStructure : formInfo) {
			String label = formStructure.getLabel();
			JSONObject item = new JSONObject();
			item.put("compt", formStructure.getCompt());
			item.put("comptId", formStructure.getId());
			JSONArray data = new JSONArray();
			result.add(item);
			if ("活动ID".equals(label)) {
				data.add(activity.getId());
				item.put("val", data);
				continue;
			}
			if ("活动名称".equals(label)) {
				data.add(activity.getName());
				item.put("val", data);
				continue;
			}
			if ("待审核数量".equals(label)) {
				item.put("val", new JSONArray());
				continue;
			}
			if ("参与学院".equals(label)) {
				data.add("信息工程学院");
				item.put("val", data);
				continue;
			}
			if ("活动分类".equals(label)) {
				data.add(activity.getActivityClassifyName());
				item.put("val", data);
				continue;
			}
			if ("活动积分".equals(label)) {
				data.add(activity.getIntegralValue());
				item.put("val", data);
				continue;
			}
			if ("单位".equals(label)) {
				data.add("积分");
				item.put("val", data);
				continue;
			}
			if ("活动预览".equals(label)) {
				data.add(activity.getPreviewUrl());
				item.put("val", data);
				continue;
			}
			if ("发起人".equals(label)) {
				data.add(activity.getCreateUserName());
				item.put("val", data);
				continue;
			}
			if ("创建者".equals(label)) {
				JSONObject user = new JSONObject();
				user.put("id", activity.getCreateUid());
				user.put("name", activity.getCreateUserName());
				data.add(user);
				item.put("idNames", data);
				continue;
			}
			if ("活动状态".equals(label)) {
				Integer status = activity.getStatus();
				Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
				data.add(statusEnum.getName());
				item.put("val", data);
				continue;
			}
		}
		return result.toJSONString();
	}

}