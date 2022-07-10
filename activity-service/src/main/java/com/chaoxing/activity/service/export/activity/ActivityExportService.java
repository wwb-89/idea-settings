package com.chaoxing.activity.service.export.activity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.dto.export.ExportDataDTO;
import com.chaoxing.activity.dto.manager.PassportUserDTO;
import com.chaoxing.activity.dto.query.ActivityManageQueryDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.model.TableField;
import com.chaoxing.activity.model.TableFieldDetail;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.PassportApiService;
import com.chaoxing.activity.service.tablefield.TableFieldQueryService;
import com.chaoxing.activity.util.DateUtils;
import com.chaoxing.activity.util.constant.UrlConstant;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**活动导出服务
 * @author wwb
 * @version ver 1.0
 * @className ActivityExportService
 * @description
 * @blame wwb
 * @date 2022-01-12 14:49:54
 */
@Slf4j
@Service
public class ActivityExportService {

	@Resource
	private TableFieldQueryService tableFieldQueryService;
	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private PassportApiService passportApiService;
	@Resource
	private CloudApiService cloudApiService;

	/**封装导出数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-10-18 16:44:08
	 * @param queryParam
	 * @return com.chaoxing.activity.dto.export.ExportDataDTO
	 */
	public ExportDataDTO packageExportData(ActivityManageQueryDTO queryParam, LoginUserDTO exportUser) {
		List<TableFieldDetail> tableFieldDetails = tableFieldQueryService.listMarketShowTableFieldDetail(queryParam.getMarketId(), TableField.Type.ACTIVITY_MANAGE_LIST, TableField.AssociatedType.ACTIVITY_MARKET);
		Page<Activity> page = activityQueryService.listManaging(new Page<>(1, Integer.MAX_VALUE), queryParam, exportUser);
		return ExportDataDTO.builder()
				.headers(listActivityHeader(tableFieldDetails))
				.data(listData(page.getRecords(), tableFieldDetails))
				.build();
	}

	private List<List<String>> listActivityHeader(List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> headers = Lists.newArrayList();
		headers.add(Lists.newArrayList("活动ID"));
		for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
			List<String> header = Lists.newArrayList();
			header.add(tableFieldDetail.getName());
			headers.add(header);
		}
		return headers;
	}

	/**获取数据
	 * @Description
	 * @author huxiaolong
	 * @Date 2021-06-01 16:19:14
	 * @param records
	 * @param tableFieldDetails
	 * @return java.util.List<java.util.List<java.lang.String>>
	 */
	private List<List<String>> listData(List<Activity> records, List<TableFieldDetail> tableFieldDetails) {
		List<List<String>> data = Lists.newArrayList();
		if (CollectionUtils.isEmpty(records)) {
			return data;
		}
		for (Activity record : records) {
			List<String> itemData = Lists.newArrayList();
			PassportUserDTO createUser = passportApiService.getByUid(record.getCreateUid());
			String createOrgName = passportApiService.getOrgName(record.getCreateFid());
			// 活动id
			itemData.add(valueToString(record.getId()));
			for (TableFieldDetail tableFieldDetail : tableFieldDetails) {
				String code = tableFieldDetail.getCode();
				switch (code) {
					case "cover":
						String coverUrl = record.getCoverUrl();
						if (StringUtils.isBlank(coverUrl) && StringUtils.isNotBlank(record.getCoverCloudId())) {
							coverUrl = cloudApiService.buildImageUrl(record.getCoverCloudId());
						}
						itemData.add(coverUrl);
						break;
					case "name":
						itemData.add(record.getName());
						break;
					case "createUserName":
						itemData.add(createUser.getRealName());
						break;
					case "createOrgName":
						itemData.add(createOrgName);
						break;
					case "signedUpNum":
						itemData.add(valueToString(record.getSignedUpNum()));
						break;
					case "status":
						itemData.add(Activity.StatusEnum.fromValue(record.getStatus()).getName());
						break;
					case "poster":
						itemData.add(UrlConstant.getPosterUrl(record.getId()));
						break;
					case "startTime":
						itemData.add(Optional.ofNullable(record.getStartTime()).map(v -> v.format(DateUtils.DATE_MINUTE_TIME_FORMATTER)).orElse(null));
						break;
					case "endTime":
						itemData.add(Optional.ofNullable(record.getEndTime()).map(v -> v.format(DateUtils.DATE_MINUTE_TIME_FORMATTER)).orElse(null));
						break;
					case "personLimit":
						itemData.add(valueToString(record.getPersonLimit()));
						break;
					case "signedInNum":
						itemData.add(valueToString(record.getSignedInNum()));
						break;
					case "signedInRate":
						itemData.add(valueToString(record.getSignedInRate()));
						break;
					case "rateNum":
						itemData.add(valueToString(record.getRateNum()));
						break;
					case "rateScore":
						itemData.add(valueToString(record.getRateScore()));
						break;
					case "qualifiedNum":
						itemData.add(valueToString(record.getQualifiedNum()));
						break;
					case "activityClassify":
						itemData.add(record.getActivityClassifyName());
						break;
					default:

				}
			}
			data.add(itemData);
		}
		return data;
	}

	private String valueToString(Object value) {
		return value == null ? "" : String.valueOf(value);
	}

}