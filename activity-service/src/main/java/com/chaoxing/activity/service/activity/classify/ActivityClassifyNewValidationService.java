package com.chaoxing.activity.service.activity.classify;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.ActivityClassifyNew;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.service.activity.market.ActivityMarketValidationService;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyNewValidationService
 * @description
 * @blame wwb
 * @date 2021-04-12 09:57:18
 */
@Slf4j
@Service
public class ActivityClassifyNewValidationService {

	@Resource
	private ActivityMarketValidationService activityMarketValidationService;
	@Resource
	private ActivityClassifyNewQueryService activityClassifyNewQueryService;

	/**是否可管理
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:11:36
	 * @param activityMarketId
	 * @param loginUser
	 * @return boolean
	*/
	public boolean isManageAble(Integer activityMarketId, LoginUserDTO loginUser) {
		ActivityMarket activityMarket = activityMarketValidationService.exist(activityMarketId);
		return Objects.equals(activityMarket.getFid(), loginUser.getFid());
	}

	/**可管理
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 11:12:05
	 * @param activityMarketId
	 * @param loginUser
	 * @return void
	*/
	public void manageAble(Integer activityMarketId, LoginUserDTO loginUser) {
		boolean manageAble = isManageAble(activityMarketId, loginUser);
		if (!manageAble) {
			throw new BusinessException("无权限");
		}
	}

	/**活动类型名称不存在
	 * @Description
	 * @author wwb
	 * @Date 2021-04-12 13:28:31
	 * @param activityMarketId
	 * @param name
	 * @param id
	 * @return void
	*/
	public void nameNotExist(Integer activityMarketId, String name, Integer id) {
		List<ActivityClassifyNew> activityClassifyNews = activityClassifyNewQueryService.listByMarketIdAndName(activityMarketId, name);
		if (CollectionUtils.isNotEmpty(activityClassifyNews)) {
			for (ActivityClassifyNew activityClassifyNew : activityClassifyNews) {
				if (!Objects.equals(activityClassifyNew.getId(), id)) {
					throw new BusinessException("名称已经存在");
				}
			}
		}
	}

	/**活动类型存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:50:27
	 * @param activityClassifyNewId
	 * @return com.chaoxing.activity.model.ActivityClassifyNew
	*/
	public ActivityClassifyNew exist(Integer activityClassifyNewId) {
		ActivityClassifyNew activityClassifyNew = activityClassifyNewQueryService.getById(activityClassifyNewId);
		Optional.ofNullable(activityClassifyNew).orElseThrow(() -> new BusinessException("活动类型不存在"));
		return activityClassifyNew;
	}

	/**活动类型存在
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:02:34
	 * @param activityClassifyNewIds
	 * @return java.util.List<com.chaoxing.activity.model.ActivityClassifyNew>
	*/
	public List<ActivityClassifyNew> exist(List<Integer> activityClassifyNewIds) {
		List<ActivityClassifyNew> activityClassifyNews = activityClassifyNewQueryService.listByIds(activityClassifyNewIds);
		if (CollectionUtils.isEmpty(activityClassifyNews)) {
			throw new BusinessException("活动类型不存在");
		}
		return activityClassifyNews;
	}

}
