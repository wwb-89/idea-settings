package com.chaoxing.activity.admin.controller.api;

import com.alibaba.fastjson.JSON;
import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.model.MarketTableField;
import com.chaoxing.activity.service.tablefield.TableFieldHandleService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**市场表格字段配置
 * @author wwb
 * @version ver 1.0
 * @className MarketTableFieldApiController
 * @description
 * @blame wwb
 * @date 2021-07-14 10:43:52
 */
@RestController
@RequestMapping("api/market/{marketId}/table-field/{tableFieldId}")
public class MarketTableFieldApiController {

	@Resource
	private TableFieldHandleService tableFieldHandleService;

	/**机构活动市场表格配置字段
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-24 14:48:49
	 * @param request
	 * @param marketId
	 * @param tableFieldId
	 * @param marketTableFieldsStr
	 * @return com.chaoxing.activity.dto.RestRespDTO
	 */
	@LoginRequired
	@RequestMapping("config")
	public RestRespDTO config(HttpServletRequest request, @PathVariable Integer marketId, @PathVariable Integer tableFieldId, @RequestParam String marketTableFieldsStr) {
		LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
		List<MarketTableField> marketTableFields = JSON.parseArray(marketTableFieldsStr, MarketTableField.class);
		tableFieldHandleService.marketTableFieldConfig(marketId, tableFieldId, marketTableFields, loginUser);
		return RestRespDTO.success();
	}

}