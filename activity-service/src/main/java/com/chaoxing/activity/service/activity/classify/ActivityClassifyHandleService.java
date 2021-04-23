package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**活动分类处理服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyHandleService
 * @description
 * @blame wwb
 * @date 2020-11-10 16:35:12
 */
@Slf4j
@Service
public class ActivityClassifyHandleService {

	@Resource
	private ActivityClassifyMapper activityClassifyMapper;

	@Resource
	private ActivityClassifyValidationService activityClassifyValidationService;
	@Resource
	private ActivityClassifyQueryService activityClassifyQueryService;

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 18:11:26
	 * @param activityClassify
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityClassify
	*/
	public ActivityClassify add(ActivityClassify activityClassify, LoginUserDTO loginUser) {
		String name = activityClassify.getName();
		if (StringUtils.isEmpty(name)) {
			throw new BusinessException("分类名称不能为空");
		}
		Integer fid = loginUser.getFid();
		activityClassifyValidationService.nameNotExist(name, null, fid);
		activityClassify.setAffiliationFid(fid);
		activityClassify.setSequence(activityClassifyMapper.getMaxSequence(fid));
		activityClassifyMapper.insert(activityClassify);
		return activityClassify;
	}

	/**修改
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 19:31:07
	 * @param activityClassify
	 * @param loginUser
	 * @return com.chaoxing.activity.model.ActivityClassify
	*/
	public ActivityClassify edit(ActivityClassify activityClassify, LoginUserDTO loginUser) {
		String name = activityClassify.getName();
		if (StringUtils.isEmpty(name)) {
			throw new BusinessException("分类名称不能为空");
		}
		Integer id = activityClassify.getId();
		activityClassifyValidationService.editAble(id, loginUser.getFid());
		activityClassifyValidationService.nameNotExist(name, activityClassify.getId(), loginUser.getFid());
		activityClassifyMapper.update(null,
				new UpdateWrapper<ActivityClassify>()
				.lambda()
				.eq(ActivityClassify::getId, id)
				.set(ActivityClassify::getName, name)
		);
		return activityClassify;
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 19:31:14
	 * @param id
	 * @param loginUser
	 * @return void
	*/
	public void delete(Integer id, LoginUserDTO loginUser) {
		activityClassifyValidationService.deleteAble(id, loginUser.getFid());
		activityClassifyMapper.deleteById(id);

	}

	/**克隆系统分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-23 18:31:44
	 * @param targetFid
	 * @return void
	*/
	public void cloneSystemClassify(Integer targetFid) {
		// 没有分类才添加
		Integer count = activityClassifyMapper.selectCount(new QueryWrapper<ActivityClassify>()
				.lambda()
				.eq(ActivityClassify::getAffiliationFid, targetFid)
		);
		if (count.compareTo(0) > 0) {
			return;
		}
		List<ActivityClassify> activityClassifies = activityClassifyQueryService.listSystem();
		if (CollectionUtils.isNotEmpty(activityClassifies)) {
			for (ActivityClassify activityClassify : activityClassifies) {
				activityClassify.setSystem(false);
				activityClassify.setAffiliationFid(targetFid);
			}
			activityClassifyMapper.batchAdd(activityClassifies);
		}
	}

	public void cloneSystemClassifyNoCheck(Integer targetFid) {
		List<ActivityClassify> existActivityClassifies = activityClassifyQueryService.listOrgOptional(targetFid);
		List<String> existActivityClassifyNames = existActivityClassifies.stream().map(ActivityClassify::getName).collect(Collectors.toList());
		Set<String> existActivityClassifyNameSet = new HashSet<>(existActivityClassifyNames);
		List<ActivityClassify> activityClassifies = activityClassifyQueryService.listSystem();
		List<ActivityClassify> adds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(activityClassifies)) {
			for (ActivityClassify activityClassify : activityClassifies) {
				if (existActivityClassifyNameSet.contains(activityClassify.getName())) {
					continue;
				}
				activityClassify.setSystem(false);
				activityClassify.setAffiliationFid(targetFid);
				adds.add(activityClassify);
			}
			activityClassifyMapper.batchAdd(activityClassifies);
		}
	}

}