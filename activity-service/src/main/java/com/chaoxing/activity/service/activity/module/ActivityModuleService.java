package com.chaoxing.activity.service.activity.module;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.module.PunchFormDTO;
import com.chaoxing.activity.dto.module.WorkFormDTO;
import com.chaoxing.activity.mapper.ActivityModuleMapper;
import com.chaoxing.activity.model.ActivityModule;
import com.chaoxing.activity.service.manager.module.PunchApiService;
import com.chaoxing.activity.service.manager.module.StarApiService;
import com.chaoxing.activity.service.manager.module.TpkApiService;
import com.chaoxing.activity.service.manager.module.WorkApiService;
import com.chaoxing.activity.util.constant.ActivityModuleConstant;
import com.chaoxing.activity.util.enums.ModuleTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**活动模块服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityModuleService
 * @description
 * @blame wwb
 * @date 2020-11-11 18:05:53
 */
@Slf4j
@Service
public class ActivityModuleService {

	@Resource
	private ActivityModuleMapper activityModuleMapper;

	@Resource
	private TpkApiService tpkApiService;
	@Resource
	private StarApiService starApiService;
	@Resource
	private WorkApiService workApiService;
	@Resource
	private PunchApiService punchApiService;

	/**批量新增
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 18:06:52
	 * @param activityModules
	 * @return void
	*/
	public void batchAdd(List<ActivityModule> activityModules) {
		activityModuleMapper.batchAdd(activityModules);
	}

	/**根据活动id删除关联的活动模块列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 18:08:32
	 * @param activityId
	 * @return void
	*/
	public void deleteByActivityId(Integer activityId) {
		activityModuleMapper.delete(new QueryWrapper<ActivityModule>()
			.lambda()
				.eq(ActivityModule::getActivityId, activityId)
		);
	}

	/**更新
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 18:11:04
	 * @param activityModule
	 * @return void
	*/
	public void update(ActivityModule activityModule) {
		activityModuleMapper.update(null, new UpdateWrapper<ActivityModule>()
			.lambda()
				.eq(ActivityModule::getId, activityModule.getId())
				.set(ActivityModule::getName, activityModule.getName())
				.set(ActivityModule::getIconCloudId, activityModule.getIconCloudId())
				.set(ActivityModule::getPcUrl, activityModule.getPcUrl())
				.set(ActivityModule::getMobileUrl, activityModule.getMobileUrl())
				.set(ActivityModule::getSequence, activityModule.getSequence())
		);
	}

	/**批量删除
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 18:30:52
	 * @param ids
	 * @return void
	*/
	public void batchDelete(List<Integer> ids) {
		activityModuleMapper.deleteBatchIds(ids);
	}

	/**根据活动id查询关联的活动模块列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-11 18:22:11
	 * @param activityId
	 * @return java.util.List<com.chaoxing.activity.model.ActivityModule>
	*/
	public List<ActivityModule> listByActivityId(Integer activityId) {
		return activityModuleMapper.selectList(new QueryWrapper<ActivityModule>()
			.lambda()
				.eq(ActivityModule::getActivityId, activityId)
		);
	}

	/**根据活动id和作品类型查询外部模块id列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-12 17:04:50
	 * @param activityId
	 * @param type
	 * @return java.util.List<java.lang.String>
	*/
	public List<String> listExternalIdsByActivityIdAndType(Integer activityId, String type) {
		List<String> result = new ArrayList<>();
		List<ActivityModule> activityModules = activityModuleMapper.selectList(new QueryWrapper<ActivityModule>()
				.lambda()
				.eq(ActivityModule::getActivityId, activityId)
				.eq(ActivityModule::getType, type)
		);
		if (CollectionUtils.isNotEmpty(activityModules)) {
			result = activityModules.stream().map(ActivityModule::getExternalId).collect(Collectors.toList());
		}
		return result;
	}

	/**生成听评课的模块
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 10:24:35
	 * @param activityId
	 * @param templateId
	 * @param name
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityModule
	*/
	public ActivityModule generateTpkModule(Integer activityId, Integer templateId, String name, LoginUserDTO loginUser) {
		Integer tpkId = tpkApiService.create(name, loginUser);
		ActivityModule activityModule = ActivityModule.builder()
				.activityId(activityId)
				.templateAppId(templateId)
				.externalId(String.valueOf(tpkId))
				.name(name)
				.type(ModuleTypeEnum.TPK.getValue())
				.iconCloudId(ActivityModuleConstant.TPK_ICON_CLOUD_ID)
				.build();
		return activityModule;
	}
	
	/**生成星阅读模块
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 10:26:30
	 * @param activityId
	 * @param templateId
	 * @param name
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityModule
	*/
	public ActivityModule generateStarModule(Integer activityId, Integer templateId, String name, LoginUserDTO loginUser) {
		Integer starId = starApiService.create(loginUser.getUid(), loginUser.getFid());
		ActivityModule activityModule = ActivityModule.builder()
				.activityId(activityId)
				.templateAppId(templateId)
				.externalId(String.valueOf(starId))
				.name(name)
				.type(ModuleTypeEnum.STAR.getValue())
				.iconCloudId(ActivityModuleConstant.STAR_ICON_CLOUD_ID)
				.build();
		return activityModule;
	}
	
	/**生成作品征集模块
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 10:27:11
	 * @param activityId
	 * @param templateId
	 * @param name
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityModule
	*/
	public ActivityModule generateWorkModule(Integer activityId, Integer templateId, String name, LoginUserDTO loginUser) {
		WorkFormDTO workForm = WorkFormDTO.builder()
				.name(name)
				.uid(loginUser.getUid())
				.wfwfid(loginUser.getFid())
				.build();
		Integer workId = workApiService.create(workForm);
		ActivityModule activityModule = ActivityModule.builder()
				.activityId(activityId)
				.templateAppId(templateId)
				.externalId(String.valueOf(workId))
				.name(name)
				.type(ModuleTypeEnum.WORK.getValue())
				.iconCloudId(ActivityModuleConstant.WORK_ICON_CLOUD_ID)
				.build();
		return activityModule;
	}

	/**生成打卡模块
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 10:29:29
	 * @param activityId
	 * @param templateId
	 * @param name
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityModule
	*/
	public ActivityModule generatePunchModule(Integer activityId, Integer templateId, String name, LoginUserDTO loginUser) {
		PunchFormDTO punchForm = PunchFormDTO.builder()
				.name(name)
				.needPubDynamic(false)
				.createUid(loginUser.getUid())
				.build();
		Integer punchId = punchApiService.create(punchForm);
		ActivityModule activityModule = ActivityModule.builder()
				.activityId(activityId)
				.templateAppId(templateId)
				.externalId(String.valueOf(punchId))
				.name(name)
				.type(ModuleTypeEnum.PUNCH.getValue())
				.iconCloudId(ActivityModuleConstant.PUNCH_ICON_CLOUD_ID)
				.build();
		return activityModule;
	}

	/**根据活动id和门户模板appId查询活动模块列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-25 00:05:42
	 * @param activityId
	 * @param templateAppId
	 * @return java.util.List<com.chaoxing.activity.model.ActivityModule>
	*/
	public List<ActivityModule> listByActivityIdAndTemplateId(Integer activityId, Integer templateAppId) {
		List<ActivityModule> activityModules = activityModuleMapper.selectList(new QueryWrapper<ActivityModule>()
				.lambda()
				.eq(ActivityModule::getActivityId, activityId)
				.eq(ActivityModule::getTemplateAppId, templateAppId)
		);
		return activityModules;
	}

}