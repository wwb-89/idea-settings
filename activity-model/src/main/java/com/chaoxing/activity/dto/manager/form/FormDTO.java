package com.chaoxing.activity.dto.manager.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 表单对象
 * @author wwb
 * @version ver 1.0
 * @className FormDTO
 * @description
 * @blame wwb
 * @date 2020-11-18 18:54:57
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FormDTO {

	private Integer formId;
	private String appName;
	private Integer aprvStatusTypeId;
	private String formName;
	private Integer aprvStatus;
	private Integer formUserId;
	private String mobile;
	private String organize;
	private Integer uid;
	private String uname;
	private Integer fid;
	private Long inserttime;
	private Long updatetime;
	private List<FormDataDTO> formData;

}