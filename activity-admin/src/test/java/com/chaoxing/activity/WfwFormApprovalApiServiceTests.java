package com.chaoxing.activity;

import com.chaoxing.activity.dto.manager.form.FormDataDTO;
import com.chaoxing.activity.service.manager.wfw.WfwFormApprovalApiService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className WfwFormApprovalApiServiceTests
 * @description
 * @blame wwb
 * @date 2021-09-02 22:52:32
 */
@SpringBootTest
public class WfwFormApprovalApiServiceTests {

	@Resource
	private WfwFormApprovalApiService wfwFormApprovalApiService;

	@Test
	public void listFormData() {
		int formId = 33542;
		Integer fid = 27725;
		List<FormDataDTO> wfwFormDtos = wfwFormApprovalApiService.listFormRecord(formId, fid);
		System.out.println(wfwFormDtos);
	}

}
