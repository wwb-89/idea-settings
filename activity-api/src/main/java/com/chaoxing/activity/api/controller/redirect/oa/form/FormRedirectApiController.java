package com.chaoxing.activity.api.controller.redirect.oa.form;

import com.chaoxing.activity.dto.manager.sign.SignDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.DataPushRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.data.DataPushRecordQueryService;
import com.chaoxing.activity.service.queue.activity.handler.WfwFormSyncActivityQueueService;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.chaoxing.activity.util.exception.WfwFormActivityNotGeneratedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.view.RedirectView;

import javax.annotation.Resource;

/**万能表单重定向服务
 * @author wwb
 * @version ver 1.0
 * @className FormRedirectApiController
 * @description
 * @blame wwb
 * @date 2022-04-01 10:51:04
 */
@Slf4j
@Controller
@RequestMapping("redirect")
public class FormRedirectApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private DataPushRecordQueryService dataPushRecordQueryService;
	@Resource
	private WfwFormSyncActivityQueueService activityFormSyncService;

	/**根据表单行id重定向到活动的详情页面
	 * @Description
	 * @author wwb
	 * @Date 2021-05-17 15:15:34
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("activity-detail/from/form")
	public RedirectView form2ActivityDetail(Integer formUserId) {
		DataPushRecord dataPushRecord = dataPushRecordQueryService.getByRecord(String.valueOf(formUserId));
		Activity activity = null;
		if (dataPushRecord != null) {
			activity = activityQueryService.getById(Integer.parseInt(dataPushRecord.getIdentify()));
		}
		if (activity == null) {
			throw new WfwFormActivityNotGeneratedException();
		} else {
			String url = activity.getPreviewUrl();
			return new RedirectView(url);
		}
	}

	/**根据表单记录id重定向到活动详情页面
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-01 15:39:34
	 * @param fid
	 * @param formId
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("activity-portal/from/wfw-form")
	public RedirectView redirectToActivityPortal(Integer fid, Integer formId, Integer formUserId) {
		Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
		return new RedirectView(activity.getPreviewUrl());
	}

	/**根据表单记录id重定向到活动门户修改主页
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-01 15:39:34
	 * @param fid
	 * @param formId
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("activity-portal/edit/from/wfw-form")
	public RedirectView redirectToActivityPortalEdit(Integer fid, Integer formId, Integer formUserId) {
		Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
		return new RedirectView(activity.getEditUrl());
	}

	/**重定向到活动管理主页
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-09-01 15:38:27
	 * @param fid
	 * @param formId
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("activity-index/from/wfw-form")
	public RedirectView redirectToActivityIndex(Integer fid, Integer formId, Integer formUserId) {
		Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
		return new RedirectView(activity.getManageUrl());
	}

	/**重定向签到管理列表
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-15 16:25:32
	 * @param fid
	 * @param formId
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("sign-in-list/from/wfw-form")
	public RedirectView redirectToSignInList(Integer fid, Integer formId, Integer formUserId) {
		Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
		return new RedirectView(SignDTO.getSignInListUrl(activity.getSignId()));
	}

	/**重定向到报名管理
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-15 16:26:10
	 * @param fid
	 * @param formId
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("sign-up-manage/from/wfw-form")
	public RedirectView redirectToSignUpManage(Integer fid, Integer formId, Integer formUserId) {
		Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
		return new RedirectView(SignDTO.getSignUpManageUrl(activity.getSignId()));
	}

	/**重定向到作品征集管理
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-15 16:26:40
	 * @param fid
	 * @param formId
	 * @param formUserId
	 * @return org.springframework.web.servlet.view.RedirectView
	 */
	@RequestMapping("work-manage/from/wfw-form")
	public RedirectView redirectToWorkManage(Integer fid, Integer formId, Integer formUserId) {
		Activity activity = activityFormSyncService.getActivityFromFormInfo(fid, formId, formUserId);
		if (activity.getOpenWork() && activity.getWorkId() != null) {
			return new RedirectView(UrlConstant.getWorkManageUrl(activity.getWorkId()));
		}
		return new RedirectView();
	}

}