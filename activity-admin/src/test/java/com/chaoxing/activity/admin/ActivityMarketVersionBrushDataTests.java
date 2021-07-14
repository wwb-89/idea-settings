package com.chaoxing.activity.admin;

import com.chaoxing.activity.mapper.ActivityMapper;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

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



}