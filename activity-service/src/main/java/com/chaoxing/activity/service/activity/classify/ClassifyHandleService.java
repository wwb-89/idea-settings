package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.activity.classify.MarketClassifyCreateParamDTO;
import com.chaoxing.activity.dto.activity.classify.MarketClassifyUpdateParamDTO;
import com.chaoxing.activity.dto.activity.classify.OrgClassifyCreateParamDTO;
import com.chaoxing.activity.dto.activity.classify.OrgClassifyUpdateParamDTO;
import com.chaoxing.activity.mapper.ClassifyMapper;
import com.chaoxing.activity.mapper.MarketClassifyMapper;
import com.chaoxing.activity.mapper.OrgClassifyMapper;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.MarketClassify;
import com.chaoxing.activity.model.OrgClassify;
import com.chaoxing.activity.service.activity.ActivityHandleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
	private MarketClassifyMapper marketClassifyMapper;

	@Resource
	private ClassifyQueryService classifyQueryService;
	@Resource
	private ClassifyValidationService classifyValidationService;
	@Resource
	private ActivityHandleService activityHandleService;

	/**给机构克隆系统分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 15:35:01
	 * @param fid
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void cloneSystemClassifyToOrg(Integer fid) {
		// 是否已经关联过活动分类
		List<OrgClassify> existOrgClassifies = classifyQueryService.listOrgClassify(fid);
		if (CollectionUtils.isNotEmpty(existOrgClassifies)) {
			return;
		}
		List<Classify> systemClassifies = classifyQueryService.listSystem();
		if (CollectionUtils.isEmpty(systemClassifies)) {
			return;
		}
		List<OrgClassify> newOrgClassifies = OrgClassify.buildFromClassifies(systemClassifies, fid);
		OrgClassify.handleSequence(newOrgClassifies, 1);
		orgClassifyMapper.batachAdd(newOrgClassifies);
	}

	/**新增活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:30:21
	 * @param classify
	 * @return com.chaoxing.activity.model.Classify
	*/
	public Classify add(Classify classify) {
		classifyMapper.insert(classify);
		return classify;
	}

	/**新增机构活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:27:08
	 * @param orgClassifyCreateParamDto
	 * @return com.chaoxing.activity.model.Classify
	*/
	@Transactional(rollbackFor = Exception.class)
	public Classify addOrgClassify(OrgClassifyCreateParamDTO orgClassifyCreateParamDto) {
		Integer fid = orgClassifyCreateParamDto.getFid();
		int maxSequence = classifyQueryService.getOrgMaxSequence(fid);
		return addOrgClassify(orgClassifyCreateParamDto, maxSequence);
	}

	/**新增机构活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 17:52:15
	 * @param orgClassifyCreateParamDto
	 * @param sequence
	 * @return com.chaoxing.activity.model.Classify
	*/
	@Transactional(rollbackFor = Exception.class)
	public Classify addOrgClassify(OrgClassifyCreateParamDTO orgClassifyCreateParamDto, Integer sequence) {
		String name = orgClassifyCreateParamDto.getName();
		Integer fid = orgClassifyCreateParamDto.getFid();
		classifyValidationService.classifyNameNotExistInOrg(name, fid);
		Classify classify = classifyQueryService.getOrAddByName(name);
		orgClassifyMapper.insert(OrgClassify.buildFromClassify(classify, orgClassifyCreateParamDto.getFid(), sequence));
		return classify;
	}

	/**修改机构活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:27:18
	 * @param orgClassifyUpdateParamDto
	 * @return com.chaoxing.activity.model.Classify
	*/
	@Transactional(rollbackFor = Exception.class)
	public Classify updateOrgClassify(OrgClassifyUpdateParamDTO orgClassifyUpdateParamDto) {
		Integer oldClassifyId = orgClassifyUpdateParamDto.getClassifyId();
		Integer fid = orgClassifyUpdateParamDto.getFid();
		OrgClassify oldOrgClassify = classifyValidationService.orgClassifyExist(fid, oldClassifyId);
		String classifyName = orgClassifyUpdateParamDto.getName();
		Classify newClassify = classifyQueryService.getOrAddByName(classifyName);
		Integer newClassifyId = newClassify.getId();
		if (Objects.equals(newClassifyId, oldClassifyId)) {
			// 新旧分类id相同，不做处理
			return newClassify;
		}
		// 删除旧的
		deleteOrgClassify(orgClassifyUpdateParamDto);
		// 新增
		addOrgClassify(OrgClassifyCreateParamDTO.build(classifyName, fid), oldOrgClassify.getSequence());
		// 更新机构创建的活动中活动分类id
		activityHandleService.updateOrgActivityClassifyId(fid, oldClassifyId, newClassifyId);
		return newClassify;
	}

	/**删除机构活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:27:29
	 * @param orgClassifyUpdateParamDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void deleteOrgClassify(OrgClassifyUpdateParamDTO orgClassifyUpdateParamDto) {
		orgClassifyMapper.delete(new LambdaUpdateWrapper<OrgClassify>()
				.eq(OrgClassify::getClassifyId, orgClassifyUpdateParamDto.getClassifyId())
				.eq(OrgClassify::getFid, orgClassifyUpdateParamDto.getFid())
		);
	}

	/**新增活动市场活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:27:41
	 * @param marketClassifyCreateParamDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void addMarketClassify(MarketClassifyCreateParamDTO marketClassifyCreateParamDto) {
		String name = marketClassifyCreateParamDto.getName();
		Integer marketId = marketClassifyCreateParamDto.getMarketId();
		classifyValidationService.classifyNameNotExistInMarket(name, marketId);
		Classify classify = classifyQueryService.getOrAddByName(name);
		// 查询当前最大的顺序
		int maxSequence = classifyQueryService.getMarketMaxSequence(marketId);
		marketClassifyMapper.insert(MarketClassify.buildFromClassify(classify, marketClassifyCreateParamDto.getMarketId(), maxSequence));
	}

	/**修改活动市场活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:27:52
	 * @param marketClassifyUpdateParamDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void updateMarketClassify(MarketClassifyUpdateParamDTO marketClassifyUpdateParamDto) {
		Integer oldClassifyId = marketClassifyUpdateParamDto.getClassifyId();
		Integer marketId = marketClassifyUpdateParamDto.getMarketId();
		MarketClassify oldMarketClassify = classifyValidationService.marketClassifyExist(marketId, oldClassifyId);
		String classifyName = marketClassifyUpdateParamDto.getName();
		Classify newClassify = classifyQueryService.getOrAddByName(classifyName);
		Integer newClassifyId = newClassify.getId();
		if (Objects.equals(newClassifyId, oldClassifyId)) {
			// 新旧分类id相同，不做处理
			return;
		}
		// 删除旧的
		deleteMarketClassify(marketClassifyUpdateParamDto);
		// 新增
		addMarketClassify(MarketClassifyCreateParamDTO.build(classifyName, marketId));
		// 更新机构创建的活动中活动分类id
		activityHandleService.updateMarketActivityClassifyId(marketId, oldClassifyId, newClassifyId);
	}

	/**删除活动市场活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:28:06
	 * @param marketClassifyUpdateParamDto
	 * @return void
	*/
	@Transactional(rollbackFor = Exception.class)
	public void deleteMarketClassify(MarketClassifyUpdateParamDTO marketClassifyUpdateParamDto) {
		marketClassifyMapper.delete(new LambdaUpdateWrapper<MarketClassify>()
				.eq(MarketClassify::getClassifyId, marketClassifyUpdateParamDto.getClassifyId())
				.eq(MarketClassify::getMarketId, marketClassifyUpdateParamDto.getMarketId())
		);
	}

	/**批量删除活动市场关联的活动分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 18:56:42
	 * @param marketId
	 * @param classifyIds
	 * @return void
	*/
	public void batchDeleteMarketClassify(Integer marketId, List<Integer> classifyIds) {
		marketClassifyMapper.delete(new LambdaUpdateWrapper<MarketClassify>()
				.eq(MarketClassify::getMarketId, marketId)
				.in(MarketClassify::getClassifyId, classifyIds)
		);
	}

	/**获取或新增活动类型
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 19:42:28
	 * @param fid
	 * @param classifyName
	 * @return com.chaoxing.activity.model.Classify
	*/
	@Transactional(rollbackFor = Exception.class)
	public Classify getOrAddOrgClassify(Integer fid, String classifyName) {
		Classify classify = classifyQueryService.getOrAddByName(classifyName);
		OrgClassify orgClassify = classifyQueryService.getByClassifyIdAndFid(classify.getId(), fid);
		if (orgClassify == null) {
			orgClassifyMapper.insert(OrgClassify.builder()
					.classifyId(classify.getId())
					.fid(fid)
					.sequence(classifyQueryService.getOrgMaxSequence(fid))
					.build());
		}
		return classify;
	}

}