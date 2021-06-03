package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.dto.FilterDTO;
import com.chaoxing.activity.mapper.ActivityClassifyMapper;
import com.chaoxing.activity.model.ActivityClassify;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

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
	public List<FilterDTO> listOrgOptions(Integer fid) {
		List<FilterDTO> result = new ArrayList<>();
		CollectionUtils.collect(activityClassifyMapper.listByFid(fid), activityClassify -> {
			FilterDTO item = new FilterDTO();
			item.setText(activityClassify.getName());
			item.setValue(String.valueOf(activityClassify.getId()));
			return item;
		}, result);
		return result;
	}

	/**查询机构可选的活动分类列表
	 * @Description
	 * @author wwb
	 * @Date 2020-11-10 19:36:37
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listOrgOptional(Integer fid) {
		return listOrgAffiliation(fid);
	}

	/**根据fid查询活动分类列表(listOrgAffiliation查询数据少了)
	* @Description
	* @author huxiaolong
	* @Date 2021-06-02 11:29:33
	* @param fid
	* @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listOrgOptionsByFid(Integer fid) {
		return activityClassifyMapper.listByFid(fid);
	}

	/**查询机构列表所能筛选的活动分类名称列表
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 13:55:20
	 * @param fids
	 * @return java.util.List<java.lang.String>
	*/
	public List<String> listOrgsOptionalName(List<Integer> fids) {
		List<String> result = Lists.newArrayList();
		List<ActivityClassify> activityClassifies = listOrgsOptional(fids);
		if (CollectionUtils.isNotEmpty(activityClassifies)) {
			List<String> activityClassifyNames = activityClassifies.stream().map(ActivityClassify::getName).collect(Collectors.toList());
			LinkedHashSet<String> set = new LinkedHashSet<>(activityClassifyNames);
			result.addAll(set);
		}
		return result;
	}

	/**查询机构列表所能筛选的活动分类列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-03-22 19:31:12
	 * @param fids
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listOrgsOptional(List<Integer> fids) {
		return listOrgsAffiliation(fids);
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
				.eq(ActivityClassify::getSystem, Boolean.TRUE)
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

	/**查询机构列表所属的活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-22 13:50:23
	 * @param fids
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listOrgsAffiliation(List<Integer> fids) {
		return activityClassifyMapper.selectList(new QueryWrapper<ActivityClassify>()
				.lambda()
				.in(ActivityClassify::getAffiliationFid, fids)
				.orderByAsc(ActivityClassify::getSequence)
		);
	}

	/**根据id列表查询
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 22:57:41
	 * @param ids
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassify>
	*/
	public List<ActivityClassify> listByIds(List<Integer> ids) {
		return activityClassifyMapper.selectList(new QueryWrapper<ActivityClassify>()
			.lambda()
				.in(ActivityClassify::getId, ids)
		);
	}

	/**根据id查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-06 19:14:23
	 * @param id
	 * @return com.chaoxing.activity.model.ActivityClassify
	*/
	public ActivityClassify getById(Integer id) {
		return activityClassifyMapper.selectOne(new QueryWrapper<ActivityClassify>()
				.lambda()
				.eq(ActivityClassify::getId, id));
	}

}