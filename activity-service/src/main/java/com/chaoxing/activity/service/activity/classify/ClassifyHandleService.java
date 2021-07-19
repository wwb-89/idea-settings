package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ClassifyMapper;
import com.chaoxing.activity.mapper.OrgClassifyMapper;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.OrgClassify;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动分类处理服务
 * @author wwb
 * @version ver 1.0
 * @className ClassifyHandleService
 * @description
 * @blame wwb
 * @date 2021-07-19 15:11:17
 */
@Slf4j
@Service
public class ClassifyHandleService {

	@Resource
	private ClassifyMapper classifyMapper;
	@Resource
	private OrgClassifyMapper orgClassifyMapper;

	@Resource
	private ClassifyQueryService classifyQueryService;

	/**查询系统分类列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 15:15:02
	 * @param 
	 * @return java.util.List<com.chaoxing.activity.model.Classify>
	*/
	public List<Classify> listSystem() {
		return classifyMapper.selectList(new LambdaQueryWrapper<Classify>()
			.eq(Classify::getSystem, true)
		);
	}

	/**给机构克隆系统分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 15:35:01
	 * @param fid
	 * @return void
	*/
	public void cloneSystemClassifyToOrg(Integer fid) {
		// 是否已经关联过活动分类
		List<OrgClassify> existOrgClassifies = classifyQueryService.listByFidIncludeDeleted(fid);
		if (CollectionUtils.isNotEmpty(existOrgClassifies)) {
			return;
		}
		List<Classify> systemClassifies = listSystem();
		if (CollectionUtils.isEmpty(systemClassifies)) {
			return;
		}
		List<OrgClassify> newOrgClassifies = OrgClassify.buildFromClassifies(systemClassifies, fid);
		OrgClassify.handleSequence(newOrgClassifies, 1);
		orgClassifyMapper.batachAdd(newOrgClassifies);
	}



}