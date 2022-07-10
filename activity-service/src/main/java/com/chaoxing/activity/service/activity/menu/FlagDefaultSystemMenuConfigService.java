package com.chaoxing.activity.service.activity.menu;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.chaoxing.activity.mapper.FlagDefaultSystemMenuConfigMapper;
import com.chaoxing.activity.model.FlagDefaultSystemMenuConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**活动标识默认系统菜单配置
 * @author wwb
 * @version ver 1.0
 * @className FlagDefaultSystemMenuConfigService
 * @description
 * @blame wwb
 * @date 2022-02-18 14:03:30
 */
@Slf4j
@Service
public class FlagDefaultSystemMenuConfigService {

	@Resource
	private FlagDefaultSystemMenuConfigMapper flagDefaultSystemMenuConfigMapper;

	/**根据flag获取，如果为空则忽略
	 * @Description 
	 * @author wwb
	 * @Date 2022-02-18 14:06:06
	 * @param flag
	 * @return java.util.List<com.chaoxing.activity.model.FlagDefaultSystemMenuConfig>
	*/
	public List<FlagDefaultSystemMenuConfig> listByFlag(String flag) {
		return flagDefaultSystemMenuConfigMapper.selectList(new LambdaQueryWrapper<FlagDefaultSystemMenuConfig>()
				.eq(FlagDefaultSystemMenuConfig::getFlag, flag)
				.eq(FlagDefaultSystemMenuConfig::getDeleted, false)
		);
	}

}