package com.chaoxing.activity.api.controller;

import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.blacklist.UserBlacklistResultDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Blacklist;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.blacklist.BlacklistQueryService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Optional;

/**黑名单
 * @author wwb
 * @version ver 1.0
 * @className BlacklistApiController
 * @description
 * @blame wwb
 * @date 2021-07-30 14:25:22
 */
@RestController
@RequestMapping("blacklist")
public class BlacklistApiController {

	@Resource
	private BlacklistQueryService blacklistQueryService;
	@Resource
	private ActivityQueryService activityQueryService;

	/**用户是否在黑名单中
	 * @Description 
	 * @author wwb
	 * @Date 2021-07-30 14:28:23
	 * @param uid
	 * @param signId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("user/{uid}/whether-in")
	public RestRespDTO isUserInBlacklist(@PathVariable Integer uid, Integer signId) {
		Activity activity = activityQueryService.getBySignId(signId);
		Integer marketId = Optional.ofNullable(activity).map(Activity::getMarketId).orElse(null);
		Blacklist blacklist = Optional.ofNullable(marketId).map(v -> blacklistQueryService.getUserBlacklist(uid, v)).orElse(null);
		return RestRespDTO.success(UserBlacklistResultDTO.buildFromBlacklist(blacklist, uid));
	}

}