package com.chaoxing.activity.dmin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.service.manager.CloudApiService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className UploadApiController
 * @description
 * @blame wwb
 * @date 2020-11-10 19:51:30
 */
@RestController
@RequestMapping("api/upload")
public class UploadApiController {

	@Resource
	private CloudApiService cloudApiService;

	/**上传图片
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 19:53:25
	 * @param file
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@PostMapping("img")
	public RestRespDTO uploadImg(MultipartFile file) {
		String result = cloudApiService.upload(file);
		return RestRespDTO.success(result);
	}

}