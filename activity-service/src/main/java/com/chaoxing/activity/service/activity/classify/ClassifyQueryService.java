package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ClassifyMapper;
import com.chaoxing.activity.mapper.OrgClassifyMapper;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.OrgClassify;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动分类查询服务
 * @author wwb
 * @version ver 1.0
 * @className ClassifyQueryService
 * @description
 * @blame wwb
 * @date 2021-07-19 15:10:55
 */
@Slf4j
@Service
public class ClassifyQueryService {

	@Resource
	private OrgClassifyMapper orgClassifyMapper;
	@Resource
	private ClassifyMapper classifyMapper;

	/**查询机构关联的活动分类（包含删除的）
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 15:34:23
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.OrgClassify>
	*/
	public List<OrgClassify> listByFidIncludeDeleted(Integer fid) {
		return orgClassifyMapper.selectList(new LambdaQueryWrapper<OrgClassify>()
				.eq(OrgClassify::getFid, fid)
		);
	}

	/**查询机构的活动分类列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:04:39
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.Classify>
	*/
	public List<Classify> listOrgClassifies(Integer fid) {
		return classifyMapper.listByFid(fid);
	}

	/**查询活动市场的活动分类列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:05:57
	 * @param marketId
	 * @return java.util.List<com.chaoxing.activity.model.Classify>
	*/
	public List<Classify> listMarketClassifies(Integer marketId) {
		return classifyMapper.listByMarketId(marketId);
	}

}