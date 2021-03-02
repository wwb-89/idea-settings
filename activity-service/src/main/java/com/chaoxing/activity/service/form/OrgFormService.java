package com.chaoxing.activity.service.form;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.chaoxing.activity.mapper.OrgFormMapper;
import com.chaoxing.activity.model.OrgForm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author wwb
 * @version ver 1.0
 * @className OrgFormService
 * @description
 * @blame wwb
 * @date 2021-02-06 19:42:25
 */
@Slf4j
@Service
public class OrgFormService {

	@Resource
	private OrgFormMapper orgFormMapper;

	/**根据fid查询
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-06 19:46:36
	 * @param fid
	 * @return com.chaoxing.activity.model.OrgForm
	*/
	public OrgForm getByFid(Integer fid) {
		return orgFormMapper.selectOne(new QueryWrapper<OrgForm>()
			.lambda()
				.eq(OrgForm::getFid, fid)
		);
	}

}