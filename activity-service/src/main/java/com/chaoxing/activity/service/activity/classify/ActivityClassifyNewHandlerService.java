package com.chaoxing.activity.service.activity.classify;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.mapper.ActivityClassifyNewMapper;
import com.chaoxing.activity.model.ActivityClassifyNew;
import com.chaoxing.activity.model.ActivityMarket;
import com.chaoxing.activity.service.activity.market.ActivityMarketQueryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动类型服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityClassifyNewService
 * @description
 * @blame wwb
 * @date 2021-04-12 09:55:52
 */
@Slf4j
@Service
public class ActivityClassifyNewHandlerService {

	@Resource
	private ActivityClassifyNewMapper activityClassifyNewMapper;

	@Resource
	private ActivityClassifyNewValidationService activityClassifyNewValidationService;
	@Resource
	private ActivityMarketQueryService activityMarketQueryService;

	/**新增活动类型
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:44:47
	 * @param activityClassifyNew
	 * @param loginUser
	 * @return void
	*/
	public void add(ActivityClassifyNew activityClassifyNew, LoginUserDTO loginUser) {
		Integer activityMarketId = activityClassifyNew.getActivityMarketId();
		activityClassifyNewValidationService.manageAble(activityMarketId, loginUser);
		String name = activityClassifyNew.getName();
		// 验证名称不存在
		activityClassifyNewValidationService.nameNotExist(activityMarketId, name, null);
		// 查询sequence
		int maxSequence = activityClassifyNewMapper.getMaxSequence(activityMarketId);
		activityClassifyNew.setSequence(maxSequence + 1);
		// 活动市场的fid
		ActivityMarket activityMarket = activityMarketQueryService.getById(activityMarketId);
		activityClassifyNew.setFid(activityMarket.getFid());
		activityClassifyNew.setCreateUid(loginUser.getUid());
		activityClassifyNew.setUpdateUid(loginUser.getUid());
		activityClassifyNewMapper.insert(activityClassifyNew);
	}

	/**修改活动类型
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:45:09
	 * @param activityClassifyNew
	 * @param loginUser
	 * @return void
	*/
	public void update(ActivityClassifyNew activityClassifyNew, LoginUserDTO loginUser) {
		Integer activityClassifyNewId = activityClassifyNew.getId();
		activityClassifyNewValidationService.exist(activityClassifyNewId);
		Integer activityMarketId = activityClassifyNew.getActivityMarketId();
		activityClassifyNewValidationService.manageAble(activityMarketId, loginUser);
		activityClassifyNewValidationService.nameNotExist(activityMarketId, activityClassifyNew.getName(), activityClassifyNewId);
		activityClassifyNewMapper.update(null, new UpdateWrapper<ActivityClassifyNew>()
			.lambda()
				.eq(ActivityClassifyNew::getId, activityClassifyNewId)
				.set(ActivityClassifyNew::getName, activityClassifyNew.getName())
				.set(ActivityClassifyNew::getUpdateUid, loginUser.getUid())
		);
	}

	/**删除活动
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 13:45:44
	 * @param activityClassifyNewId
	 * @param loginUser
	 * @return void
	*/
	public void delete(Integer activityClassifyNewId, LoginUserDTO loginUser) {
		ActivityClassifyNew activityClassifyNew = activityClassifyNewValidationService.exist(activityClassifyNewId);
		activityClassifyNewValidationService.manageAble(activityClassifyNew.getActivityMarketId(), loginUser);
		activityClassifyNewMapper.update(null, new UpdateWrapper<ActivityClassifyNew>()
				.lambda()
				.eq(ActivityClassifyNew::getId, activityClassifyNewId)
				.set(ActivityClassifyNew::getDeleted, true)
		);
	}

	/**批量删除
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-12 14:03:00
	 * @param activityClassifyNewIds
	 * @param loginUser
	 * @return void
	*/
	public void batchDelete(List<Integer> activityClassifyNewIds, LoginUserDTO loginUser) {
		List<ActivityClassifyNew> exist = activityClassifyNewValidationService.exist(activityClassifyNewIds);
		ActivityClassifyNew activityClassifyNew = exist.get(0);
		Integer activityMarketId = activityClassifyNew.getActivityMarketId();
		activityClassifyNewValidationService.manageAble(activityMarketId, loginUser);
		activityClassifyNewMapper.update(null, new UpdateWrapper<ActivityClassifyNew>()
				.lambda()
				.eq(ActivityClassifyNew::getActivityMarketId, activityMarketId)
				.in(ActivityClassifyNew::getId, activityClassifyNewIds)
				.set(ActivityClassifyNew::getDeleted, true)
		);
	}

}