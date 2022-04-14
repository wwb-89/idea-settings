package com.chaoxing.activity;

import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.service.manager.wfw.WfwAreaApiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;

/**微服务区域服务
 * @author wwb
 * @version ver 1.0
 * @className WfwAreaApiServiceTests
 * @description
 * @blame wwb
 * @date 2022-02-14 15:45:30
 */
@Slf4j
@SpringBootTest
public class WfwAreaApiServiceTests {

	@Resource
	private WfwAreaApiService wfwAreaApiService;

	@Test
	public void listByCode() {
		String areaCode = "0017";
		List<WfwAreaDTO> wfwAreaDtos = wfwAreaApiService.listByCode(areaCode);
		for (WfwAreaDTO wfwAreaDto : wfwAreaDtos) {
			System.out.println(wfwAreaDto.getFid());
		}
	}

}