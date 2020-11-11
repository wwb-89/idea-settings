package com.chaoxing.activity.service.activity.module;

import com.chaoxing.activity.dto.ModuleDTO;
import com.chaoxing.activity.util.enums.ModuleEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className ModuleService
 * @description
 * @blame wwb
 * @date 2020-11-11 10:51:24
 */
@Slf4j
@Service
public class ModuleService {

	/**所有的模块
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 10:53:43
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.dto.ModuleDTO>
	*/
	public List<ModuleDTO> list() {
		List<ModuleDTO> result = new ArrayList<>();
		ModuleEnum[] values = ModuleEnum.values();
		for (ModuleEnum value : values) {
			result.add(ModuleDTO.builder()
					.name(value.getName())
					.value(value.getValue())
			.build());
		}
		return result;
	}

}
