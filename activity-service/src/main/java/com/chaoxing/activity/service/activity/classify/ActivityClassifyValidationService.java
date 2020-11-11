package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.util.constant.CommonConstant;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**活动验证服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyValidationService
 * @description
 * @blame wwb
 * @date 2020-11-10 19:32:34
 */
@Slf4j
@Service
public class ActivityClassifyValidationService {

	@Resource
	private ActivityClassifyMapper activityClassifyMapper;

	/**是否可修改
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 19:31:27
	 * @param id
	 * @param fid
	 * @return com.chaoxing.activity.model.ActivityClassify
	 */
	public ActivityClassify editAble(Integer id, Integer fid) {
		ActivityClassify activityClassify = activityClassifyMapper.selectById(id);
		Optional.ofNullable(activityClassify).orElseThrow(() -> new BusinessException("分类不存在"));
		if (!Objects.equals(activityClassify.getAffiliationFid(), fid)) {
			throw new BusinessException("只能修改本机构创建的分类");
		}
		return activityClassify;
	}

	/**是否可删除
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 19:31:34
	 * @param id
	 * @param fid
	 * @return com.chaoxing.activity.model.ActivityClassify
	 */
	public ActivityClassify deleteAble(Integer id, Integer fid) {
		ActivityClassify activityClassify = activityClassifyMapper.selectById(id);
		Optional.ofNullable(activityClassify).orElseThrow(() -> new BusinessException("分类不存在"));
		if (!Objects.equals(activityClassify.getAffiliationFid(), fid)) {
			throw new BusinessException("只能删除本机构创建的分类");
		}
		return activityClassify;
	}

	/**名称不存在
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 19:31:43
	 * @param name
	 * @param id
	 * @param fid
	 * @return void
	 */
	public void nameNotExist(String name, Integer id, Integer fid) {
		List<ActivityClassify> activityClassifies = new ArrayList<>();
		List<ActivityClassify> systemActivityClassifies = activityClassifyMapper.selectList(
				new LambdaQueryWrapper<ActivityClassify>()
						.eq(ActivityClassify::getAffiliationFid, CommonConstant.SYSTEM_FID)
						.eq(ActivityClassify::getName, name)
		);
		if (CollectionUtils.isNotEmpty(systemActivityClassifies)) {
			activityClassifies.addAll(systemActivityClassifies);
		}
		List<ActivityClassify> orgActivityClassifies = activityClassifyMapper.selectList(
				new LambdaQueryWrapper<ActivityClassify>()
						.eq(ActivityClassify::getAffiliationFid, fid)
						.eq(ActivityClassify::getName, name)
		);
		if (CollectionUtils.isNotEmpty(orgActivityClassifies)) {
			activityClassifies.addAll(orgActivityClassifies);
		}
		if (CollectionUtils.isNotEmpty(activityClassifies)) {
			for (ActivityClassify activityClassify : activityClassifies) {
				if (!Objects.equals(activityClassify.getId(), id)) {
					throw new BusinessException("分类名称已存在");
				}
			}
		}
	}

}