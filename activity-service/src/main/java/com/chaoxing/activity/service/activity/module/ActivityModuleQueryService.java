package com.chaoxing.activity.service.activity.module;

import com.chaoxing.activity.mapper.ActivityModuleMapper;
import com.chaoxing.activity.util.enums.ModuleTypeEnum;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**活动模块查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleQueryService
 * @description
 * @blame wwb
 * @date 2021-01-13 19:08:03
 */
@Slf4j
@Service
public class ActivityModuleQueryService {

	@Resource
	private ActivityModuleMapper activityModuleMapper;

	/**作品征集id列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:18:57
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listWorkActivityId(List<Integer> fids) {
		return listExternalId(ModuleTypeEnum.WORK.getValue(), fids);
	}
	
	/**打卡id列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-01-13 19:43:16
	 * @param fids
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listPunchId(List<Integer> fids) {
		return listExternalId(ModuleTypeEnum.PUNCH.getValue(), fids);
	}

	private List<Integer> listExternalId(String type, List<Integer> fids) {
		List<Integer> result = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(fids)) {
			List<String> externalIds = activityModuleMapper.listExternalIdByFids(type, fids);
			externalIds = externalIds.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
			for (String externalId : externalIds) {
				result.add(Integer.parseInt(externalId));
			}
		}
		return result;
	}

}