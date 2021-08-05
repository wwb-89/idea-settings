package com.chaoxing.activity;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.chaoxing.activity.dto.OperateUserDTO;
import com.chaoxing.activity.mapper.ActivityMapper;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.Template;
import com.chaoxing.activity.service.activity.market.MarketHandleService;
import com.chaoxing.activity.service.activity.template.TemplateQueryService;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**活动市场版本刷数据
 * @author wwb
 * @version ver 1.0
 * @className ActivityMarketVersionBrushDataTests
 * @description 刷数据流程
 * 1、数据库的结构更新到最新
 * 2、查询所有的活动按照机构和活动标识分组
 * 3、给机构创建活动市场和模版
 * 4、给所有的活动补齐市场id和模版id
 * 5、给所有的报名签到补齐来源id（模版组件id）
 * @blame wwb
 * @date 2021-07-14 16:03:02
 */
@SpringBootTest
public class ActivityMarketVersionBrushDataTests {

	@Resource
	private ActivityMapper activityMapper;
	@Resource
	private MarketHandleService marketHandleService;
	@Resource
	private TemplateQueryService templateQueryService;

	@Test
	public void createMarket() {
		// 查询所有的活动
		List<Activity> allActivities = activityMapper.selectList(new QueryWrapper<>());
		if (CollectionUtils.isEmpty(allActivities)) {
			return;
		}
		OperateUserDTO operateUserDto = OperateUserDTO.build(25418810);
		// 找到所有的fid以及关联的活动标识列表key:fid, value:activityFlags
		Map<Integer, Set<String>> fidActivityFlagSetRelation = allActivities.stream().collect(Collectors.groupingBy(Activity::getCreateFid, Collectors.mapping(Activity::getActivityFlag, Collectors.toSet())));
		fidActivityFlagSetRelation.forEach((fid, activityFlags) -> activityFlags.forEach(activityFlag -> {
			marketHandleService.add(fid, activityFlag, operateUserDto);
		}));
	}

	@Test
	public void activityBindMarketIdAndTemplateId() {
		List<Activity> allActivities = activityMapper.selectList(new QueryWrapper<>());
		if (CollectionUtils.isEmpty(allActivities)) {
			return;
		}
		allActivities.forEach(activity -> {
			// 根据活动标识找到机构下的该模版
			Template template = templateQueryService.getOrgTemplateByActivityFlag(activity.getCreateFid(), Activity.ActivityFlagEnum.fromValue(activity.getActivityFlag()));
			activityMapper.update(null, new LambdaUpdateWrapper<Activity>()
					.eq(Activity::getId, activity.getId())
					.set(Activity::getMarketId, template.getMarketId())
					.set(Activity::getTemplateId, template.getId())
			);
		});

	}

}