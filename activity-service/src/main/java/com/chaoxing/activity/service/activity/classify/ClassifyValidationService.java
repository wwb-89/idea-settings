package com.chaoxing.activity.service.activity.classify;

import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.model.MarketClassify;
import com.chaoxing.activity.model.OrgClassify;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**活动分类验证服务
 * @author wwb
 * @version ver 1.0
 * @className ClassifyValidationService
 * @description
 * @blame wwb
 * @date 2021-07-19 15:12:31
 */
@Slf4j
@Service
public class ClassifyValidationService {

	@Resource
	private ClassifyQueryService classifyQueryService;

	/**机构不存在指定名称的分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:44:26
	 * @param name
	 * @param fid
	 * @return void
	*/
	public void classifyNameNotExistInOrg(String name, Integer fid) {
		Classify classify = classifyQueryService.getByName(name);
		if (classify == null) {
			return;
		}
		Integer classifyId = classify.getId();
		OrgClassify orgClassify = classifyQueryService.getByClassifyIdAndFid(classifyId, fid);
		if (orgClassify != null) {
			throw new BusinessException("分类已经存在");
		}
	}

	/**机构活动分类关联存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 17:17:19
	 * @param fid
	 * @param classifyId
	 * @return com.chaoxing.activity.model.OrgClassify
	*/
	public OrgClassify orgClassifyExist(Integer fid, Integer classifyId) {
		OrgClassify orgClassify = classifyQueryService.getByClassifyIdAndFid(classifyId, fid);
		if (orgClassify == null) {
			throw new BusinessException("分类不存在");
		}
		return orgClassify;
	}

	/**活动市场不存在指定名称的分类
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 16:45:48
	 * @param name
	 * @param marketId
	 * @return void
	*/
	public void classifyNameNotExistInMarket(String name, Integer marketId) {
		Classify classify = classifyQueryService.getByName(name);
		if (classify == null) {
			return;
		}
		Integer classifyId = classify.getId();
		MarketClassify marketClassify = classifyQueryService.getByClassifyIdAndMarketId(classifyId, marketId);
		if (marketClassify != null) {
			throw new BusinessException("分类已经存在");
		}
	}

	/**活动市场关联活动分类存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-19 17:18:18
	 * @param marketId
	 * @param classifyId
	 * @return com.chaoxing.activity.model.MarketClassify
	*/
	public MarketClassify marketClassifyExist(Integer marketId, Integer classifyId) {
		MarketClassify marketClassify = classifyQueryService.getByClassifyIdAndMarketId(classifyId, marketId);
		if (marketClassify == null) {
			throw new BusinessException("分类不存在");
		}
		return marketClassify;
	}

}