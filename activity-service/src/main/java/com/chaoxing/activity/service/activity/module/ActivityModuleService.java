package com.chaoxing.activity.service.activity.module;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.pageShowModel;

import com.chaoxing.activity.dto.query.ActivityQueryDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.mapper.ActivityModuleMapper;

import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.model.ActivityModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

	@Autowired
	private ActivityClassifyMapper classifyMapper;

	@Autowired
	private ActivityMapper activityMapper;

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


	/**
	 * @Description 活动展示模块，展示所有的活动模块分页
	 * @author dkm
	 * @Date 2020-11-19 14：50：30
	 * @param
	 * @param
	 * @return java.util.List<pageShowModel>
	 */
	public List<pageShowModel> getModelMsgPage(Integer current, Integer limit) {
		Page<Activity> page = new Page<>(current,limit);
		Page<Activity> activityPage = activityMapper.selectPage(page, null);

		List<Activity> activityList = activityPage.getRecords();
		long total = activityPage.getTotal();
		List<pageShowModel> list = new ArrayList<>();
		pageShowModel pageShowModel = null;
		for (Activity ac: activityList) {
			pageShowModel = new pageShowModel();
			pageShowModel.setCoverCloudId(ac.getCoverCloudId());
			pageShowModel.setName(ac.getName());
			pageShowModel.setStatus(ac.getStatus());
			pageShowModel.setTime(ac.getStartDate()+"~"+ac.getEndDate());
			pageShowModel.setCreateOrgName(ac.getCreateOrgName());
			pageShowModel.setAddress(ac.getAddress());
			list.add(pageShowModel);
		}
		return list;
	}

	public Long getTotal(Integer current,Integer limit) {
		Page<Activity> page = new Page<>(current,limit);
		Page<Activity> activityPage = activityMapper.selectPage(page, null);
		long total = activityPage.getTotal();
		return total;
	}



	/*
	* 多条件查询的 根据查询条件 分页来查询，封装再返回*/
	public List<pageShowModel> getpageCondition(Integer current, Integer limit, ActivityQueryDTO query) {
		//创建page对象
		Page<Activity> page = new Page<>(current,limit);
		//创建构造器
		QueryWrapper<Activity> wrapper = new QueryWrapper<>();
		//多条件组合查询
		if(!StringUtils.isEmpty(query)){
			String activityClassifyName = query.getActivityClassifyName();//活动分类的的id
			if(!StringUtils.isEmpty(activityClassifyName)){
				ActivityClassify classify = classifyMapper.selectOne(new QueryWrapper<ActivityClassify>().eq("name", activityClassifyName));
				System.out.println(activityClassifyName);
				Integer classifyId = classify.getId();
				System.out.println(classifyId);
				if(!StringUtils.isEmpty(classifyId)){
					wrapper.eq("activity_classify_id",classifyId);
				}
			}
//			ActivityClassify classify = classifyMapper.selectOne(new QueryWrapper<ActivityClassify>().eq("name", activityClassifyName));
//			Integer classifyId = classify.getId();
			String area = query.getArea();//活动地点
			Integer status = query.getStatus();//活动状态
			System.out.println(status);
			String date = query.getDate();//时间
			//构建条件
//			if(!StringUtils.isEmpty(classifyId)){
//				wrapper.like("activity_classify_id",classifyId);
//			}
			if(!StringUtils.isEmpty(area)){
				wrapper.like("address",area);
			}
			if(!StringUtils.isEmpty(status)){
				wrapper.eq("status",status);
			}
			if(!StringUtils.isEmpty(date)){
				wrapper.like("start_date",date);
			}
		}
		//调用mapper来查询
		Page<Activity> ConditionActivityPage = activityMapper.selectPage(page, wrapper);
		List<Activity> recordList = ConditionActivityPage.getRecords();
		long total = ConditionActivityPage.getTotal();//查询总数
		List<pageShowModel> list = new ArrayList<>();
		pageShowModel pageShowModel = null;
		for (Activity ac: recordList) {
			pageShowModel = new pageShowModel();
			pageShowModel.setCoverCloudId(ac.getCoverCloudId());
			pageShowModel.setName(ac.getName());
			pageShowModel.setStatus(ac.getStatus());
			pageShowModel.setTime(ac.getStartDate()+"~"+ac.getEndDate());
			pageShowModel.setCreateOrgName(ac.getCreateOrgName());
			pageShowModel.setAddress(ac.getAddress());
			list.add(pageShowModel);
		}
		return list;

	}
}