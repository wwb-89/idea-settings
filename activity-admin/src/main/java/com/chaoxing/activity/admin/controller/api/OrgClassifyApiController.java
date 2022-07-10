package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.classify.OrgClassifyCreateParamDTO;
import com.chaoxing.activity.dto.activity.classify.OrgClassifyUpdateParamDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**机构活动分类服务
 * @author wwb
 * @version ver 1.0
 * @className OrgClassifyApiController
 * @description
 * @blame wwb
 * @date 2020-11-10 15:06:32
 */
@RestController
@RequestMapping("api/org/{fid}/classify")
public class OrgClassifyApiController {

	@Resource
	private ClassifyHandleService classifyHandleService;

	/**新增活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 10:07:57
	 * @param request
	 * @param orgClassifyCreateParamDto
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@PostMapping("add")
	public RestRespDTO add(HttpServletRequest request, OrgClassifyCreateParamDTO orgClassifyCreateParamDto) {
		Classify classify = classifyHandleService.addOrgClassify(orgClassifyCreateParamDto);
		return RestRespDTO.success(classify);
	}

	/**修改活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 10:08:27
	 * @param request
	 * @param orgClassifyUpdateParamDto
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("edit")
	public RestRespDTO edit(HttpServletRequest request, OrgClassifyUpdateParamDTO orgClassifyUpdateParamDto) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		Classify classify = classifyHandleService.updateOrgClassify(orgClassifyUpdateParamDto);
		return RestRespDTO.success(classify);
	}

	/**删除活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-17 10:10:18
	 * @param request
	 * @param fid
	 * @param id
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("{id}/delete")
	public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer fid, @PathVariable Integer id) {
		classifyHandleService.deleteOrgClassify(OrgClassifyUpdateParamDTO.builder()
				.classifyId(id)
				.fid(fid)
				.build());
		return RestRespDTO.success();
	}

	/**排序
	 * @Description 
	 * @author wwb
	 * @Date 2022-01-07 17:07:33
	 * @param request
	 * @param fid
	 * @param classifyIds
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("sort")
	public RestRespDTO sort(HttpServletRequest request, @PathVariable Integer fid, @RequestParam(value = "classifyIds[]") Integer[] classifyIds) {
		classifyHandleService.orgClassifySort(fid, Lists.newArrayList(classifyIds));
		return RestRespDTO.success();
	}

}