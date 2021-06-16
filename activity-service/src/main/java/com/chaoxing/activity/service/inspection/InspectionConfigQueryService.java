package com.chaoxing.activity.service.inspection;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.InspectionConfigDetailMapper;
import com.chaoxing.activity.mapper.InspectionConfigMapper;
import com.chaoxing.activity.model.InspectionConfig;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**考核配置查询服务
 * @author wwb
 * @version ver 1.0
 * @className InspectionConfigQueryService
 * @description
 * @blame wwb
 * @date 2021-06-16 15:26:41
 */
@Slf4j
@Service
public class InspectionConfigQueryService {

	@Resource
	private InspectionConfigMapper inspectionConfigMapper;
	@Resource
	private InspectionConfigDetailMapper inspectionConfigDetailMapper;

	/**根据活动id查询考核配置
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-16 15:30:35
	 * @param activityId
	 * @return com.chaoxing.activity.model.InspectionConfig
	*/
	public InspectionConfig getByActivityId(Integer activityId) {
		List<InspectionConfig> inspectionConfigs = inspectionConfigMapper.selectList(new QueryWrapper<InspectionConfig>()
				.lambda()
				.eq(InspectionConfig::getActivityId, activityId)
		);
		if (CollectionUtils.isNotEmpty(inspectionConfigs)) {
			return inspectionConfigs.get(0);
		}
		return null;
	}

	/**根据活动id查询考核配置详情列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-16 15:33:33
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.InspectionConfigDetail>
	*/
	public List<InspectionConfigDetail> listDetailByActivityId(Integer activityId) {
		InspectionConfig inspectionConfig = getByActivityId(activityId);
		List<InspectionConfigDetail> inspectionConfigDetails = listDetailByConfig(inspectionConfig);
		return inspectionConfigDetails;
	}

	/**根据考核配置查询考核配置详情列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-16 15:36:29
	 * @param inspectionConfig
	 * @return java.util.List<com.chaoxing.activity.model.InspectionConfigDetail>
	*/
	public List<InspectionConfigDetail> listDetailByConfig(InspectionConfig inspectionConfig) {
		List<InspectionConfigDetail> inspectionConfigDetails = null;
		if (inspectionConfig != null) {
			inspectionConfigDetails = inspectionConfigDetailMapper.selectList(new QueryWrapper<InspectionConfigDetail>()
					.lambda()
					.eq(InspectionConfigDetail::getConfigId, inspectionConfig.getId())
			);
		}
		if (CollectionUtils.isEmpty(inspectionConfigDetails)) {
			inspectionConfigDetails = Lists.newArrayList();
		}
		return inspectionConfigDetails;
	}

}
