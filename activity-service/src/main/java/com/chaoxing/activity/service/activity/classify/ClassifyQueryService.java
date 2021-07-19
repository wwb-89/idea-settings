package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.ClassifyMapper;
import com.chaoxing.activity.mapper.MarketClassifyMapper;
import com.chaoxing.activity.mapper.OrgClassifyMapper;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.MarketClassify;
import com.chaoxing.activity.model.OrgClassify;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

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
	private ClassifyMapper classifyMapper;
	@Resource
	private OrgClassifyMapper orgClassifyMapper;
	@Resource
	private MarketClassifyMapper marketClassifyMapper;

	@Resource
	private ClassifyHandleService classifyHandleService;

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

	/**查询机构的活动分类关联
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 18:09:23
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.model.OrgClassify>
	*/
	public List<OrgClassify> listOrgClassify(Integer fid) {
		return orgClassifyMapper.selectList(new LambdaQueryWrapper<OrgClassify>()
				.eq(OrgClassify::getFid, fid)
				.orderByAsc(OrgClassify::getSequence)
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

	/**根据名称查询活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:31:29
	 * @param classifyName
	 * @return com.chaoxing.activity.model.Classify
	*/
	public Classify getByName(String classifyName) {
		List<Classify> classifies = classifyMapper.selectList(new LambdaQueryWrapper<Classify>()
				.eq(Classify::getName, classifyName)
		);
		return Optional.ofNullable(classifies).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**根据分类名称获取或创建
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:33:01
	 * @param classifyName
	 * @return com.chaoxing.activity.model.Classify
	*/
	public Classify getOrAddByName(String classifyName) {
		Classify classify = getByName(classifyName);
		if (classify == null) {
			classify = Classify.buildFromName(classifyName);
			classify = classifyHandleService.add(classify);
		}
		return classify;
	}

	/**查询机构关联的某个活动分类id
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:43:13
	 * @param classifyId
	 * @param fid
	 * @return com.chaoxing.activity.model.OrgClassify
	*/
	public OrgClassify getByClassifyIdAndFid(Integer classifyId, Integer fid) {
		List<OrgClassify> orgClassifies = orgClassifyMapper.selectList(new LambdaQueryWrapper<OrgClassify>()
				.eq(OrgClassify::getClassifyId, classifyId)
				.eq(OrgClassify::getFid, fid)
		);
		return Optional.ofNullable(orgClassifies).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**查询市场关联的某个活动分类id
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:43:26
	 * @param classifyId
	 * @param marketId
	 * @return com.chaoxing.activity.model.MarketClassify
	*/
	public MarketClassify getByClassifyIdAndMarketId(Integer classifyId, Integer marketId) {
		List<MarketClassify> marketClassifies = marketClassifyMapper.selectList(new LambdaQueryWrapper<MarketClassify>()
				.eq(MarketClassify::getClassifyId, classifyId)
				.eq(MarketClassify::getMarketId, marketId)
		);
		return Optional.ofNullable(marketClassifies).orElse(Lists.newArrayList()).stream().findFirst().orElse(null);
	}

	/**获取机构最大的顺序
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 17:49:22
	 * @param fid
	 * @return int
	*/
	public int getOrgMaxSequence(Integer fid) {
		return orgClassifyMapper.getMaxSequenceByFid(fid);
	}

	/**获取活动市场最大的顺序
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 17:47:34
	 * @param marketId
	 * @return int
	*/
	public int getMarketMaxSequence(Integer marketId) {
		return marketClassifyMapper.getMaxSequenceByMarketId(marketId);
	}

}