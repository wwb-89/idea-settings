package com.chaoxing.activity.service.org;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.OrgConfigMapper;
import com.chaoxing.activity.model.OrgConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**机构配置服务
 * @author wwb
 * @version ver 1.0
 * @className OrgConfigService
 * @description
 * @blame wwb
 * @date 2021-06-17 11:16:29
 */
@Slf4j
@Service
public class OrgConfigService {

	@Resource
	private OrgConfigMapper orgConfigMapper;

	/**根据fid查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-17 11:18:19
	 * @param fid
	 * @return com.chaoxing.activity.model.OrgConfig
	*/
	public OrgConfig getByFid(Integer fid) {
		List<OrgConfig> orgConfigs = orgConfigMapper.selectList(new QueryWrapper<OrgConfig>()
			.lambda()
				.eq(OrgConfig::getFid, fid)
		);
		if (CollectionUtils.isNotEmpty(orgConfigs)) {
			return orgConfigs.get(0);
		}
		return null;
	}

	/**配置
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-17 11:20:39
	 * @param orgConfig
	 * @return void
	*/
	public void config(OrgConfig orgConfig) {
		Integer fid = orgConfig.getFid();
		OrgConfig existOrgConfig = getByFid(fid);
		if (existOrgConfig == null) {
			orgConfigMapper.insert(orgConfig);
		} else {
			orgConfigMapper.update(null, new UpdateWrapper<OrgConfig>()
					.lambda()
					.eq(OrgConfig::getId, existOrgConfig.getId())
					.set(OrgConfig::getSignUpScopeType, orgConfig.getSignUpScopeType())
			);
		}
	}

}
