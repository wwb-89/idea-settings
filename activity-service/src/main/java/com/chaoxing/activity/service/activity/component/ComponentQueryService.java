package com.chaoxing.activity.service.activity.component;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ComponentFieldMapper;
import com.chaoxing.activity.mapper.ComponentMapper;
import com.chaoxing.activity.model.Component;
import com.chaoxing.activity.model.ComponentField;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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
	@Autowired
	private ComponentFieldMapper componentFieldMapper;

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
	
	/**根据模板id查询组件，系统模板+自身组件
	 * 若templateId为null，查询的则仅仅是系统组件
	* @Description 
	* @author huxiaolong
	* @Date 2021-08-30 17:00:17
	* @param 
	* @return java.util.List<com.chaoxing.activity.model.Component>
	*/
	public List<Component> listByTemplateId(Integer templateId) {
		LambdaQueryWrapper<Component> wrapper = new LambdaQueryWrapper<Component>().eq(Component::getSystem, Boolean.TRUE);
		if (templateId != null) {
			wrapper.or().eq(Component::getTemplateId, templateId);
		}
		return componentMapper.selectList(wrapper);
	}

	/**查询组件列表，若组件列表中存在自定义选择组件，将选项列表一起封装进组件中
	* @Description
	* @author huxiaolong
	* @Date 2021-09-26 14:38:23
	* @param templateId
	* @return java.util.List<com.chaoxing.activity.model.Component>
	*/
	public List<Component> listWithOptionsByTemplateId(Integer templateId) {
		List<Component> components = listByTemplateId(templateId);
		return packageCustomChooseOptions(components);
	}

	/**遍历组件列表，若存在自定义选择组件，查询封装选项列表
	* @Description 
	* @author huxiaolong
	* @Date 2021-09-26 14:40:52
	* @param components
	* @return java.util.List<com.chaoxing.activity.model.Component>
	*/
	private List<Component> packageCustomChooseOptions(List<Component> components) {
		// 获取选择组件自定义的选项
		components.forEach(v -> {
			if (Component.TypeEnum.chooseType(v.getType()) && Objects.equals(v.getDataOrigin(), Component.DataOriginEnum.CUSTOM.getValue())) {
				// 自定义选项值列表
				List<ComponentField> componentFields = componentFieldMapper.selectList(new QueryWrapper<ComponentField>()
						.lambda()
						.eq(ComponentField::getComponentId, v.getId()));
				v.setComponentFields(componentFields);
			}
		});
		return components;

	}

}
