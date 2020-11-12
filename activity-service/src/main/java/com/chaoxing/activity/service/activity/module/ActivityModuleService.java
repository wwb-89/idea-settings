package com.chaoxing.activity.service.activity.module;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.ActivityModuleMapper;
import com.chaoxing.activity.model.ActivityModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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

}