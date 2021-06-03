package com.chaoxing.activity.service.stat;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.query.admin.UserStatSummaryQueryDTO;
import com.chaoxing.activity.mapper.TableFieldDetailMapper;
import com.chaoxing.activity.mapper.UserStatSummaryMapper;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.model.UserStatSummary;
import com.chaoxing.activity.service.manager.OrganizationalStructureApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * @author wwb
 * @version ver 1.0
 * @className UserStatSummaryQueryService
 * @description
 * @blame wwb
 * @date 2021-06-03 10:37:24
 */
@Slf4j
@Service
public class UserStatSummaryQueryService {

	@Resource
	private TableFieldDetailMapper tableFieldDetailMapper;
	@Resource
	private UserStatSummaryMapper userStatSummaryMapper;

	@Resource
	private OrganizationalStructureApiService organizationalStructureApiService;
	@Resource
	private TableFieldQueryService tableFieldQueryService;
	@Resource
	private PassportApiService passportApiService;

	/**
	 * 分页查询用户统计
	 *
	 * @param page
	 * @param userStatSummaryQuery
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
	 * @Description
	 * @author wwb
	 * @Date 2021-05-28 15:57:41
	 */
	public Page paging(Page page, UserStatSummaryQueryDTO userStatSummaryQuery) {
		Integer groupId = userStatSummaryQuery.getGroupId();
		Integer fid = userStatSummaryQuery.getFid();
		List<Integer> orgUids = organizationalStructureApiService.listOrgUid(fid);
		userStatSummaryQuery.setOrgUids(orgUids);
		List<Integer> groupUids = Lists.newArrayList();
		if (groupId != null) {
			groupUids = organizationalStructureApiService.listOrgGroupUid(fid, groupId, userStatSummaryQuery.getGroupLevel());
			if (CollectionUtils.isEmpty(groupUids)) {
				// 给一个不存在的uid
				groupUids.add(-1);
			}
		}
		userStatSummaryQuery.setGroupUids(groupUids);
		Integer orderTableFieldId = userStatSummaryQuery.getOrderTableFieldId();
		if (orderTableFieldId == null) {
			userStatSummaryQuery.setOrderField("");
		} else {
			TableFieldDetail tableFieldDetail = tableFieldDetailMapper.selectById(orderTableFieldId);
			userStatSummaryQuery.setOrderField(tableFieldDetail.getCode());
		}
		page = userStatSummaryMapper.paging(page, userStatSummaryQuery);
		return page;
	}

	/**
	 * 获取导出数据
	 *
	 * @param userStatSummaryQuery
	 * @return com.chaoxing.activity.dto.export.ExportDataDTO
	 * @Description
	 * @author wwb
	 * @Date 2021-06-03 11:09:53
	 */
	public ExportDataDTO getExportDataDTO(UserStatSummaryQueryDTO userStatSummaryQuery) {
		Page page = new Page(1, Integer.MAX_VALUE);
		page = paging(page, userStatSummaryQuery);
		List<UserStatSummary> records = page.getRecords();
		// 封装头部和内容
		Integer fid = userStatSummaryQuery.getFid();
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listOrgShowTableFieldDetail(fid, TableField.Type.USER_STAT, TableField.AssociatedType.ORG);
		List<List<String>> headers = listExportheaders(tableFieldDetails);
		List<List<String>> data = listExportData(records, tableFieldDetails, fid, userStatSummaryQuery.getOrgUids());
		return ExportDataDTO.builder()
				.headers(headers)
				.data(data)
				.build();
	}

	/**获取导出的数据的header
	 * @Description 根据fid查询出配置的table fieldDetail
	 * @author wwb
	 * @Date 2021-06-03 13:16:51
	 * @param tableFieldDetails
	 * @return java.util.List<java.util.List<java.lang.String>>
	*/
	private List<List<String>> listExportheaders(List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> headers = Lists.newArrayList();
		for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
			List<String> header = Lists.newArrayList();
			header.add(tableFieldDetail.getName());
			headers.add(header);
		}
		return headers;
	}

	/**获取导出的数据的数据
	 * @Description
	 * @author wwb
	 * @Date 2021-06-03 14:11:19
	 * @param records
	 * @param tableFieldDetails
	 * @param fid 查询名称
	 * @param allUids 机构下的所有用户uid列表
	 * @return java.util.List<java.util.List<java.lang.String>>
	*/
	private List<List<String>> listExportData(List<UserStatSummary> records, List<TableFieldDetail> tableFieldDetails, Integer fid, List<Integer> allUids) {
		List<List<String>> data = Lists.newArrayList();
		String orgName = passportApiService.getOrgName(fid);
		for (UserStatSummary record : records) {
			List<String> dataItem = Lists.newArrayList();
			for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
				String code = tableFieldDetail.getCode();
				switch (code) {
					case "realName":
						dataItem.add(record.getRealName());
						break;
					case "studentNo":
						dataItem.add(record.getStudentNo());
						break;
					case "organizationStructure":
						dataItem.add(record.getOrganizationStructure());
						break;
					case "participateActivityNum":
						dataItem.add(String.valueOf(record.getParticipateActivityNum()));
						break;
					case "signedInNum":
						dataItem.add(String.valueOf(record.getSignedInNum()));
						break;
					case "signedInRate":
						BigDecimal signedInRate = record.getSignedInRate();
						String ratingDescription = signedInRate.toString();
						if (signedInRate.compareTo(BigDecimal.ZERO) > 0) {
							ratingDescription += "%";
						}
						dataItem.add(ratingDescription);
						break;
					case "ratingNum":
						dataItem.add(String.valueOf(record.getRatingNum()));
						break;
					case "qualifiedNum":
						dataItem.add(String.valueOf(record.getQualifiedNum()));
						break;
					case "participateTimeLength":
						dataItem.add(String.valueOf(record.getParticipateTimeLength()));
						break;
					case "orgName":
						if (allUids.contains(record.getUid())) {
							dataItem.add(orgName);
						} else {
							dataItem.add("");
						}
						break;
					case "integral":
						dataItem.add(String.valueOf(record.getIntegral()));
						break;
					default:
						dataItem.add("");
				}
			}
			data.add(dataItem);
		}
		return data;
	}

}