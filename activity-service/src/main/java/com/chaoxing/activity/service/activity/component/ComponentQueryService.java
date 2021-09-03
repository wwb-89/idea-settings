package com.chaoxing.activity.service.activity.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.model.Component;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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

	/**根据id列表查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-18 20:28:37
	 * @param componentIds
	 * @return java.util.List<com.chaoxing.activity.model.Component>
	*/
	public List<Component> listByIds(List<Integer> componentIds) {
		return componentMapper.selectList(new LambdaQueryWrapper<Component>()
				.in(Component::getId, componentIds)
		);
	}

	/**查询系统组件id与名称的关联
	 * @Description 
	 * @author wwb
	 * @Date 2021-08-23 10:14:51
	 * @param 
	 * @return java.util.Map<java.lang.Integer,java.lang.String>
	*/
	public Map<Integer, String> getSystemComponentIdNameRelation() {
		List<Component> components = componentMapper.selectList(new LambdaQueryWrapper<Component>()
				.eq(Component::getSystem, true)
		);
		return Optional.ofNullable(components).orElse(Lists.newArrayList()).stream().collect(Collectors.toMap(Component::getId, Component::getName, (v1, v2) -> v2));
	}

	/**查询系统组件code与名称的关联
	 * @Description
	 * @author wwb
	 * @Date 2021-08-23 10:15:36
	 * @param
	 * @return java.util.Map<java.lang.String,java.lang.String>
	*/
	public Map<String, String> getSystemComponentCodeNameRelation() {
		List<Component> components = componentMapper.selectList(new LambdaQueryWrapper<Component>()
				.eq(Component::getSystem, true)
		);
		return Optional.ofNullable(components).orElse(Lists.newArrayList()).stream().collect(Collectors.toMap(Component::getCode, Component::getName, (v1, v2) -> v2));
	}

}
