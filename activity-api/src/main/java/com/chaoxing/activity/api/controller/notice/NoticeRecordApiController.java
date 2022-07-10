package com.chaoxing.activity.api.controller.notice;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.NoticeRecord;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.notice.record.NoticeRecordHandleService;
import com.chaoxing.activity.service.notice.record.NoticeRecordQueryService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**通知记录api
 * @author wwb
 * @version ver 1.0
 * @className NoticeRecordApiController
 * @description
 * @blame wwb
 * @date 2022-02-12 10:19:15
 */
@Slf4j
@RestController
@RequestMapping("notice/record")
public class NoticeRecordApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private NoticeRecordHandleService noticeRecordHandleService;
	@Resource
	private NoticeRecordQueryService noticeRecordQueryService;

	/**记录报名通知
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-12 10:21:14
	 * @param activityId
	 * @param title
	 * @param content
	 * @param timestamp
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sign-up/save")
	public RestRespDTO recordSignUpNotice(@RequestParam Integer activityId, @RequestParam String title, @RequestParam String content, @RequestParam Long timestamp) {
		Activity activity = activityQueryService.getById(activityId);
		if (activity != null) {
			NoticeRecord noticeRecord = NoticeRecord.builder()
					.type(NoticeRecord.TypeEnum.SIGN_UP.getValue())
					.activityId(activityId)
					.activityCreateFid(activity.getCreateFid())
					.activityFlag(activity.getActivityFlag())
					.title(title)
					.content(content)
					.time(DateUtils.timestamp2Date(timestamp))
					.build();
			noticeRecordHandleService.add(noticeRecord);
		}
		return RestRespDTO.success();
	}

	/**分页查询通知记录
	 * @Description 厦门项目
	 * @author wwb
	 * @Date 2022-02-15 15:51:50
	 * @param request
	 * @param fid
	 * @param flags
	 * @param type
	 * @param content
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("paging")
	public RestRespDTO paging(HttpServletRequest request, Integer fid, String flags, String type, @RequestParam(defaultValue = "false") Boolean content) {
		Page<NoticeRecord> page = HttpServletRequestUtils.buid(request);
		page = noticeRecordQueryService.paging(page, fid, flags, type, content);
		return RestRespDTO.success(page);
	}

	/**根据通知记录id查询
	 * @Description 厦门项目
	 * @author wwb
	 * @Date 2022-02-15 16:08:00
	 * @param id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{id}")
	public RestRespDTO get(@PathVariable Integer id) {
		NoticeRecord noticeRecord = noticeRecordQueryService.getById(id);
		return RestRespDTO.success(noticeRecord);
	}

}