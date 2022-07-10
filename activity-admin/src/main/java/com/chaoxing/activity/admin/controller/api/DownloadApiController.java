package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.service.manager.CloudApiService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className DownloadApiController
 * @description
 * @blame wwb
 * @date 2021-06-03 15:16:21
 */
@Controller
@RequestMapping("api/download")
public class DownloadApiController {

	@Resource
	private CloudApiService cloudApiService;

	/**重定向到云盘资源下载
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-03 15:17:43
	 * @param cloudId
	 * @return java.lang.String
	*/
	@RequestMapping("cloud/{cloudId}")
	public String redirect(@PathVariable String cloudId) {
		String fileDownloadUrl = cloudApiService.getFileDownloadUrl(cloudId);
		return "redirect:" + fileDownloadUrl;
	}

}
