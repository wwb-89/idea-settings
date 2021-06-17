package com.chaoxing.activity.service.repoconfig;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.chaoxing.activity.mapper.OrgDataRepoConfigDetailMapper;
import com.chaoxing.activity.mapper.OrgDataRepoConfigMapper;
import com.chaoxing.activity.model.OrgDataRepoConfig;
import com.chaoxing.activity.model.OrgDataRepoConfigDetail;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgDataRepoConfigHandleService
 * @description
 * @blame wwb
 * @date 2021-06-08 17:04:08
 */
@Slf4j
@Service
public class OrgDataRepoConfigHandleService {

	@Resource
	private OrgDataRepoConfigMapper orgDataRepoConfigMapper;
	@Resource
	private OrgDataRepoConfigDetailMapper orgDataRepoConfigDetailMapper;

	@Resource
	private OrgDataRepoConfigQueryService orgDataRepoConfigQueryService;

	/**新增或更新机构数据仓库配置信息
	 * @Description 
	 * @author wwb
	 * @Date 2021-06-08 17:29:53
	 * @param fid
	 * @param repo
	 * @param dataType
	 * @param repoType
	 * @return void
	*/
	public void addOrUpdate(Integer fid, String repo, OrgDataRepoConfigDetail.DataTypeEnum dataType, OrgDataRepoConfigDetail.RepoTypeEnum repoType) {
		OrgDataRepoConfig orgDataRepoConfig = orgDataRepoConfigQueryService.getByFid(fid);
		if (orgDataRepoConfig == null) {
			orgDataRepoConfig = new OrgDataRepoConfig();
			orgDataRepoConfig.setFid(fid);
			orgDataRepoConfigMapper.insert(orgDataRepoConfig);
		}
		if (StringUtils.isBlank(repo)) {
			// 删除配置详情
			orgDataRepoConfigDetailMapper.delete(new UpdateWrapper<OrgDataRepoConfigDetail>()
				.lambda()
					.eq(OrgDataRepoConfigDetail::getConfigId, orgDataRepoConfig.getId())
					.eq(OrgDataRepoConfigDetail::getDataType, dataType.getValue())
					.eq(OrgDataRepoConfigDetail::getRepoType, repoType.getValue())
			);
		} else {
			Integer configId = orgDataRepoConfig.getId();
			OrgDataRepoConfigDetail orgDataRepoConfigDetail = orgDataRepoConfigQueryService.getOrgDataRepoConfigDetail(configId, dataType, repoType);
			if (orgDataRepoConfigDetail == null) {
				orgDataRepoConfigDetail = OrgDataRepoConfigDetail.builder()
						.configId(configId)
						.dataType(dataType.getValue())
						.repoType(repoType.getValue())
						.repo(repo)
						.build();
				orgDataRepoConfigDetailMapper.insert(orgDataRepoConfigDetail);
			} else {
				orgDataRepoConfigDetailMapper.update(null, new UpdateWrapper<OrgDataRepoConfigDetail>()
						.lambda()
						.eq(OrgDataRepoConfigDetail::getId, orgDataRepoConfigDetail.getId())
						.set(OrgDataRepoConfigDetail::getRepo, repo)
				);
			}
		}
	}

}
