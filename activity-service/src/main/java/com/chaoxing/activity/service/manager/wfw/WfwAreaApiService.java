package com.chaoxing.activity.service.manager.wfw;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.dto.manager.wfw.WfwAreaDTO;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.util.exception.BusinessException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**微服务区域服务
 * @author wwb
 * @version ver 1.0
 * @className WfwRegionalArchitectureApiService
 * @description
 * @blame wwb
 * @date 2020-11-12 11:22:58
 */
@Slf4j
@Service
public class WfwAreaApiService {

	/** 根据fid获取架构的code */
	private static final String GET_AREA_BY_FID_URL = "http://guanli.chaoxing.com/siteInter/siteHierarchy?fid=%s";
	/** 根据code获取架构 */
	private static final String GET_AREA_BY_CODE_URL = "http://guanli.chaoxing.com/siteInter/siteHierarchy?code=%s&pageSize=%s";

	@Resource
	private RestTemplate restTemplate;
	@Resource
	private PassportApiService passportApiService;

	/**根据fid查询该机构下的层级机构
	 * @Description
	 * 先根据fid查询层级code列表，再根据code列表分别查询层级架构
	 * @author wwb
	 * @Date 2020-08-24 13:27:21
	 * @param fid
	 * @return java.util.List<com.chaoxing.activity.common.manager.dto.RegionalArchitectureDTO>
	 */
	public List<WfwAreaDTO> listByFid(Integer fid) {
		List<WfwAreaDTO> result;
		List<String> codes = listCodeByFid(fid);
		if (CollectionUtils.isEmpty(codes)) {
			result = Lists.newArrayList();
		} else {
			String code = codes.get(0);
			result = listByCode(code);
		}
		if (CollectionUtils.isEmpty(result)) {
			// 构建一个当前单位的范围
			result.add(buildWfwRegionalArchitecture(fid));
		}
		return result;
	}

	/**根据fid查询该机构下的层级机构，并以树结构返回结构
	* @Description 
	* @author huxiaolong
	* @Date 2021-06-10 18:12:14
	* @param fid
	* @return java.util.List<com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO>
	*/
	public List<WfwAreaDTO> listWfwAreaTreesByFid(Integer fid) {
		List<WfwAreaDTO> wfwRegionalArchitectures = listByFid(fid);
		List<WfwAreaDTO> trees = buildTree(fid, wfwRegionalArchitectures);

		for (WfwAreaDTO item : wfwRegionalArchitectures) {
			Boolean existChild = Optional.ofNullable(item.getExistChild()).orElse(Boolean.FALSE);
			// 有父节点，且有子节点，那么当前节点一定为区域节点，其父节点存在区域
			if (item.getPid() != null && existChild) {
				handleExistAreaNode(item.getPid(), trees);
			}
		}
		return trees;
	}

	/**给区域节点设置区域属性
	* @Description
	* @author huxiaolong
	* @Date 2021-06-15 16:17:53
	* @param pid
	* @param trees
	* @return void
	*/
	private void handleExistAreaNode(Integer pid, List<WfwAreaDTO> trees) {
		for (WfwAreaDTO item : trees) {
			if (Objects.equals(pid, item.getId())) {
				item.setExistArea(Boolean.TRUE);
				return;
			}
			if (CollectionUtils.isNotEmpty(item.getChildren())) {
				handleExistAreaNode(pid, item.getChildren());
			}
		}

	}

	private List<WfwAreaDTO> buildTree(Integer fid, List<WfwAreaDTO> regionalArchitectureList) {
		List<WfwAreaDTO> trees = new ArrayList<>();
		for (WfwAreaDTO treeNode : regionalArchitectureList) {
			// 将当前fid的机构节点父级设为0(无父级)，以便于构造树
			if (Objects.equals(fid, treeNode.getFid())) {
				treeNode.setPid(0);
			}
			if (Objects.equals(treeNode.getPid(), 0)) {
				trees.add(treeNode);
			}
			for (WfwAreaDTO it : regionalArchitectureList) {
				if (it.getPid() != null && !Objects.equals(it.getPid(), 0) && Objects.equals(it.getPid(), treeNode.getId())) {
					if (treeNode.getChildren() == null) {
						treeNode.setChildren(new ArrayList<>());
					}
					treeNode.getChildren().add(it);
				}
			}
		}
		return trees;
	}
	
	/**构建一个层级架构
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-20 13:00:08
	 * @param fid
	 * @return com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO
	*/
	public WfwAreaDTO buildWfwRegionalArchitecture(Integer fid) {
		String orgName = passportApiService.getOrgName(fid);
		return WfwAreaDTO.builder()
				.id(0)
				.name(orgName)
				.pid(0)
				.code("")
				.links(orgName)
				.level(1)
				.fid(fid)
				.existChild(Boolean.FALSE)
				.sort(1)
				.build();
	}

	/**根据层级区域编码查询层级架构
	 * @Description 
	 * @author wwb
	 * @Date 2020-12-02 11:18:23
	 * @param code
	 * @return java.util.List<com.chaoxing.activity.dto.manager.WfwRegionalArchitectureDTO>
	*/
	public List<WfwAreaDTO> listByCode(String code) {
		List<WfwAreaDTO> regionalArchitectures = Lists.newArrayList();
		String url = String.format(GET_AREA_BY_CODE_URL, code, Integer.MAX_VALUE);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		Boolean status = jsonObject.getBooleanValue("status");
		status = Optional.ofNullable(status).orElse(Boolean.FALSE);
		if (!status) {
			log.error("根据code:{}查询所属层级架构列表失败", code);
			throw new BusinessException("未查询到层级架构");
		}
		String jsonlistStr = jsonObject.getString("list");
		List<WfwAreaDTO> items = JSON.parseArray(jsonlistStr, WfwAreaDTO.class);
		if (CollectionUtils.isNotEmpty(items)) {
			regionalArchitectures.addAll(items);
		}
		return regionalArchitectures;
	}

	/**根据fid查询所在层级架构code列表
	 * @Description
	 * @author wwb
	 * @Date 2020-08-24 13:31:16
	 * @param fid
	 * @return java.util.List<java.lang.String>
	 */
	public List<String> listCodeByFid(Integer fid) {
		List<String> codes = Lists.newArrayList();
		String url = String.format(GET_AREA_BY_FID_URL, fid);
		String result = restTemplate.getForObject(url, String.class);
		JSONObject jsonObject = JSON.parseObject(result);
		boolean status = jsonObject.getBooleanValue("status");
		if (!status) {
			log.error("根据fid:{}查询所属层级架构列表失败", fid);
			throw new BusinessException("未查询到层级架构");
		}
		String jsonlistStr = jsonObject.getString("list");
		List<WfwAreaDTO> regionalArchitectures = JSON.parseArray(jsonlistStr, WfwAreaDTO.class);
		if (CollectionUtils.isNotEmpty(regionalArchitectures)) {
			for (WfwAreaDTO regionalArchitecture : regionalArchitectures) {
				String code = regionalArchitecture.getCode();
				if (!codes.contains(code)) {
					codes.add(code);
				}
			}
		}
		return codes;
	}

	/**查询下级机构fid列表
	 * @Description 
	 * @author wwb
	 * @Date 2021-04-15 20:08:32
	 * @param fid
	 * @return java.util.List<java.lang.Integer>
	*/
	public List<Integer> listSubFid(Integer fid) {
		List<Integer> fids = Lists.newArrayList();
		List<WfwAreaDTO> wfwRegionalArchitectures = listByFid(fid);
		if (CollectionUtils.isNotEmpty(wfwRegionalArchitectures)) {
			List<Integer> subFids = wfwRegionalArchitectures.stream().map(WfwAreaDTO::getFid).collect(Collectors.toList());
			fids.addAll(subFids);
		} else {
			fids.add(fid);
		}
		return fids;
	}

}