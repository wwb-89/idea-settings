package com.chaoxing.activity.admin.controller.api;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.activity.classify.MarketClassifyCreateParamDTO;
import com.chaoxing.activity.dto.activity.classify.MarketClassifyUpdateParamDTO;
import com.chaoxing.activity.model.Classify;
import com.chaoxing.activity.service.activity.classify.ClassifyHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import com.google.common.collect.Lists;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**活动市场活动类型api服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyNewApiController
 * @description
 * @blame wwb
 * @date 2021-04-12 09:53:22
 */
@RestController
@RequestMapping("api/market/{marketId}/classify")
public class MarketClassifyApiController {

	@Resource
	private ClassifyHandleService classifyHandleService;

	/**新增
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:15
	 * @param request
	 * @param marketClassifyCreateParamDto
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@LoginRequired
	@RequestMapping("add")
	public RestRespDTO add(HttpServletRequest request, @PathVariable Integer marketId, MarketClassifyCreateParamDTO marketClassifyCreateParamDto) {
		marketClassifyCreateParamDto.setMarketId(marketId);
		classifyHandleService.addMarketClassify(marketClassifyCreateParamDto);
		return RestRespDTO.success();
	}

	/**修改
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:23
	 * @param request
	 * @param marketClassifyUpdateParamDto
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("update")
	public RestRespDTO update(HttpServletRequest request, MarketClassifyUpdateParamDTO marketClassifyUpdateParamDto) {
		Classify classify = classifyHandleService.updateMarketClassify(marketClassifyUpdateParamDto);
		return RestRespDTO.success(classify);
	}

	/**删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:33
	 * @param request
	 * @param marketId
	 * @param classifyId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("{classifyId}/delete")
	public RestRespDTO delete(HttpServletRequest request, @PathVariable Integer marketId, @PathVariable Integer classifyId) {
		classifyHandleService.deleteMarketClassify(MarketClassifyUpdateParamDTO.builder()
				.marketId(marketId)
				.classifyId(classifyId)
				.build());
		return RestRespDTO.success();
	}

	/**批量删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:06:44
	 * @param request
	 * @param marketId
	 * @param classifyIds
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("delete/batch")
	public RestRespDTO batchDelete(HttpServletRequest request, @PathVariable Integer marketId, @RequestParam(value = "classifyIds[]") Integer[] classifyIds) {
		classifyHandleService.batchDeleteMarketClassify(marketId, Lists.newArrayList(classifyIds));
		return RestRespDTO.success();
	}

}