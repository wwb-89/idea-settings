package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.chaoxing.activity.util.constant.CommonConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**活动分类查询服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyQueryService
 * @description
 * @blame wwb
 * @date 2020-11-10 19:35:13
 */
@Slf4j
@Service
public class ActivityClassifyQueryService {

	@Resource
	private ActivityClassifyMapper activityClassifyMapper;

	/**查询机构可选的活动分类列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 19:36:37
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listOrgOptional(Integer fid) {
		List<ActivityClassify> activityClassifies = new ArrayList<>();
		List<ActivityClassify> systemActivityClassifies = listSystem();
		if (CollectionUtils.isNotEmpty(systemActivityClassifies)) {
			activityClassifies.addAll(systemActivityClassifies);
		}
		List<ActivityClassify> orgActivityClassifies = listOrgAffiliation(fid);
		if (CollectionUtils.isNotEmpty(orgActivityClassifies)) {
			activityClassifies.addAll(orgActivityClassifies);
		}
		return activityClassifies;
	}

	/**查询系统的活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 19:43:28
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listSystem() {
		return activityClassifyMapper.selectList(new QueryWrapper<ActivityClassify>()
				.lambda()
				.eq(ActivityClassify::getAffiliationFid, CommonConstant.SYSTEM_FID)
				.orderByAsc(ActivityClassify::getSequence)
		);
	}

	/**查询机构所属的活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-10 19:43:43
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listOrgAffiliation(Integer fid) {
		return activityClassifyMapper.selectList(new QueryWrapper<ActivityClassify>()
				.lambda()
				.eq(ActivityClassify::getAffiliationFid, fid)
				.orderByAsc(ActivityClassify::getSequence)
		);
	}

}