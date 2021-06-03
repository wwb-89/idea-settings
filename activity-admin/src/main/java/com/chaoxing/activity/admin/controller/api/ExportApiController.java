package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.ExportRecord;
import com.chaoxing.activity.service.export.ExportRecordHandleService;
import com.chaoxing.activity.util.HttpServletRequestUtils;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author wwb
 * @version ver 1.0
 * @className ExportApiController
 * @description
 * @blame wwb
 * @date 2021-06-02 20:13:48
 */
@RestController
@RequestMapping("api/export")
public class ExportApiController {

	@Resource
	private ExportRecordHandleService exportRecordService;

	/**导出活动统计数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-05-31 17:21:10
	 * @param request
	 * @param queryParamStr
	 * @param exportType
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("")
	public RestRespDTO exportActivityStatSummary(HttpServletRequest request, String queryParamStr, String exportType) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		String createIp = HttpServletRequestUtils.getClientIp(request);
		exportRecordService.add(queryParamStr, loginUser.getUid(), createIp, exportType);
		return RestRespDTO.success();
	}

}
