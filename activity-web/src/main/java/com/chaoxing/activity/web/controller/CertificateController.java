package com.chaoxing.activity.web.controller;

import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.CertificateIssue;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.certificate.CertificateQueryService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import java.util.Optional;

/**证书服务
 * @author wwb
 * @version ver 1.0
 * @className CertificateController
 * @description
 * @blame wwb
 * @date 2022-03-10 20:20:57
 */
@Slf4j
@Controller
@RequestMapping("certificate")
public class CertificateController {

	@Resource
	private CertificateQueryService certificateQueryService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private PassportApiService passportApiService;

	/**防伪认证页面
	 * @Description 
	 * @author wwb
	 * @Date 2022-03-10 20:34:50
	 * @param model
	 * @param no
	 * @return java.lang.String
	*/
	@RequestMapping("{no}/anti-fake")
	public String antiFake(Model model, @PathVariable String no) {
		CertificateIssue certificateIssue = certificateQueryService.getByNo(no);
		Optional.ofNullable(certificateIssue).orElseThrow(() -> new BusinessException("证书不存在"));
		Integer activityId = certificateIssue.getActivityId();
		Activity activity = activityQueryService.getById(activityId);
		model.addAttribute("certificateIssue", certificateIssue);
		model.addAttribute("activity", activity);
		// 获取用户的姓名
		PassportUserDTO passportUser = passportApiService.getByUid(certificateIssue.getUid());
		model.addAttribute("realName", passportUser.getRealName());
		return "mobile/certificate/anti-fake";
	}

}