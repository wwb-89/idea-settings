package com.chaoxing.activity.service.form;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityFormRecordMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityFormRecord;
import com.chaoxing.activity.model.OrgForm;
import com.chaoxing.activity.service.manager.FormApiService;
import com.chaoxing.activity.service.manager.FormAssistService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

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
	private FormAssistService formAssistService;
	@Resource
	private FormApiService formApiService;

	/**新增
	 * @Description
	 * @author wwb
	 * @Date 2021-02-06 19:48:31
	 * @param activity
	 * @return void
	*/
	public void add(Activity activity) {
		Integer createFid = activity.getCreateFid();
		OrgForm orgForm = orgFormService.getByFid(createFid);
		if (orgForm == null) {
			return;
		}
		Integer formId = orgForm.getFormId();
		String formInfo = formApiService.getFormInfo(createFid, formId);
		String activityFillFormData = formAssistService.getActivityFillFormData(formInfo, activity);
		Integer formUserId = formApiService.fillForm(createFid, formId, activity.getCreateUid(), activityFillFormData);
		ActivityFormRecord activityFormRecord = ActivityFormRecord.builder()
				.activityId(activity.getId())
				.formId(formId)
				.formUserId(formUserId)
				.build();
		activityFormRecordMapper.insert(activityFormRecord);
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
		ActivityFormRecord activityFormRecord = activityFormRecordMapper.selectOne(new QueryWrapper<ActivityFormRecord>()
				.lambda()
				.eq(ActivityFormRecord::getActivityId, activityId)
		);
		if (activityFormRecord == null) {
			return;
		}
		formApiService.deleteFormRecord(activityFormRecord.getFormId(), activityFormRecord.getFormUserId());
		activityFormRecordMapper.delete(new QueryWrapper<ActivityFormRecord>()
			.lambda()
				.eq(ActivityFormRecord::getId, activityFormRecord.getActivityId())
		);
	}

}