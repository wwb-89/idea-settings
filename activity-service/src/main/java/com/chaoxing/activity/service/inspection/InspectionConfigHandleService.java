package com.chaoxing.activity.service.inspection;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.InspectionConfigDetailMapper;
import com.chaoxing.activity.mapper.InspectionConfigMapper;
import com.chaoxing.activity.model.InspectionConfig;
import com.chaoxing.activity.model.InspectionConfigDetail;
import com.chaoxing.activity.service.activity.ActivityValidationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**考核配置处理服务
 * @author wwb
 * @version ver 1.0
 * @className InspectionConfigHandleService
 * @description
 * @blame wwb
 * @date 2021-06-16 15:26:50
 */
@Slf4j
@Service
public class InspectionConfigHandleService {

	@Resource
	private InspectionConfigMapper inspectionConfigMapper;
	@Resource
	private InspectionConfigDetailMapper inspectionConfigDetailMapper;

	@Resource
	private ActivityValidationService activityValidationService;
	@Resource
	private InspectionConfigQueryService inspectionConfigQueryService;

	/**配置
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-16 17:50:12
	 * @param inspectionConfig
	 * @param inspectionConfigDetails
	 * @param loginUser
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void config(InspectionConfig inspectionConfig, List<InspectionConfigDetail> inspectionConfigDetails, LoginUserDTO loginUser) {
		Integer activityId = inspectionConfig.getActivityId();
		activityValidationService.manageAble(activityId, loginUser.getUid());
		InspectionConfig existInspectionConfig = inspectionConfigQueryService.getByActivityId(activityId);
		if (existInspectionConfig == null) {
			// 新增
			inspectionConfigMapper.insert(inspectionConfig);
		} else {
			// 更新
			inspectionConfigMapper.update(null, new UpdateWrapper<InspectionConfig>()
				.lambda()
					.eq(InspectionConfig::getId, existInspectionConfig.getId())
					.set(InspectionConfig::getPassDecideWay, inspectionConfig.getPassDecideWay())
					.set(InspectionConfig::getDecideValue, inspectionConfig.getDecideValue())
			);
		}
		Integer configId = inspectionConfig.getId();
		for (InspectionConfigDetail inspectionConfigDetail : inspectionConfigDetails) {
			inspectionConfigDetail.setConfigId(configId);
			if (inspectionConfigDetail.getId() == null) {
				inspectionConfigDetailMapper.insert(inspectionConfigDetail);
			} else {
				inspectionConfigDetailMapper.update(null, new UpdateWrapper<InspectionConfigDetail>()
					.lambda()
						.eq(InspectionConfigDetail::getId, inspectionConfigDetail.getId())
						.set(InspectionConfigDetail::getScore, inspectionConfigDetail.getScore())
						.set(InspectionConfigDetail::getUpperLimit, inspectionConfigDetail.getUpperLimit())
						.set(InspectionConfigDetail::getDeleted, inspectionConfigDetail.getDeleted())
				);
			}
		}

	}

}
