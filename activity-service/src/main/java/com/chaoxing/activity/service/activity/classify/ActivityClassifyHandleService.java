package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

}