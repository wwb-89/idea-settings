package com.chaoxing.activity.service.activity.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.model.Component;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ComponentQueryService
 * @description
 * @blame wwb
 * @date 2021-07-15 16:03:20
 */
@Slf4j
@Service
public class ComponentQueryService {

	@Resource
	private ComponentMapper componentMapper;

	/**根据code查询系统组件
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-15 16:05:03
	 * @param code
	 * @return com.chaoxing.activity.model.Component
	*/
	public Component getSystemComponentByCode(String code) {
		List<Component> components = componentMapper.selectList(new LambdaQueryWrapper<Component>()
				.eq(Component::getSystem, true)
				.eq(Component::getCode, code)
		);
		return Optional.ofNullable(components).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

}
