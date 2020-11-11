package com.chaoxing.activity.web.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**活动模块
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleApiController
 * @description
 * @blame wwb
 * @date 2020-11-11 10:08:18
 */
@RestController
@RequestMapping("api/activity/module")
public class ActivityModuleApiController {


	@PostMapping("sign/new")
	public RestRespDTO createSign() {
		return RestRespDTO.success();
	}

	@PostMapping("sign/edit")
	public RestRespDTO editSign() {
		return RestRespDTO.success();
	}

	@PostMapping("work/new")
	public RestRespDTO createWork() {
		return RestRespDTO.success();
	}

	@PostMapping("work/edit")
	public RestRespDTO editWork() {
		return RestRespDTO.success();
	}

	@PostMapping("punch/new")
	public RestRespDTO createPunch() {
		return RestRespDTO.success();
	}

	@PostMapping("punch/edit")
	public RestRespDTO editPunch() {
		return RestRespDTO.success();
	}

	@PostMapping("evaluation/new")
	public RestRespDTO createEvaluation() {
		return RestRespDTO.success();
	}

	@PostMapping("evaluation/edit")
	public RestRespDTO editEvaluation() {
		return RestRespDTO.success();
	}

	@PostMapping("star/new")
	public RestRespDTO createStar() {
		return RestRespDTO.success();
	}

	@PostMapping("star/edit")
	public RestRespDTO editStar() {
		return RestRespDTO.success();
	}

	@PostMapping("tpk/new")
	public RestRespDTO createTpk() {
		return RestRespDTO.success();
	}

	@PostMapping("tpk/edit")
	public RestRespDTO editTpk() {
		return RestRespDTO.success();
	}

	@PostMapping("group/new")
	public RestRespDTO createGroup() {
		return RestRespDTO.success();
	}

	@PostMapping("group/edit")
	public RestRespDTO editGroup() {
		return RestRespDTO.success();
	}

}
