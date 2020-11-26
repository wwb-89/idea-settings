package com.chaoxing.activity.api.mh;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.chaoxing.activity.dto.RestRespDTO;
import com.chaoxing.activity.dto.manager.SignParticipationDTO;
import com.chaoxing.activity.dto.mh.MhGeneralAppResultDataDTO;
import com.chaoxing.activity.model.Activity;
import com.chaoxing.activity.service.activity.ActivityQueryService;
import com.chaoxing.activity.service.manager.CloudApiService;
import com.chaoxing.activity.service.manager.MhApiService;
import com.chaoxing.activity.service.manager.module.SignApiService;
import com.chaoxing.activity.util.constant.DateTimeFormatterConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**门户应用api接口
 * @author wwb
 * @version ver 1.0
 * @className ActivityMhAppApiController
 * @description
 * @blame wwb
 * @date 2020-11-24 17:35:03
 */
@RestController
@RequestMapping("mh")
public class ActivityMhAppApiController {

	@Resource
	private ActivityQueryService activityQueryService;
	@Resource
	private CloudApiService cloudApiService;
	@Resource
	private SignApiService signApiService;
	@Resource
	private MhApiService mhApiService;

	/**活动封面
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 19:02:11
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/cover")
	public RestRespDTO activityCover(@PathVariable Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		String coverCloudId = activity.getCoverCloudId();
		String coverUrl = cloudApiService.getCloudImgUrl(coverCloudId);
		JSONObject jsonObject = new JSONObject();
		MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
		mhGeneralAppResultDataDTO.setType(3);
		mhGeneralAppResultDataDTO.setOrsUrl("");
		mhGeneralAppResultDataDTO.setPop(0);
		mhGeneralAppResultDataDTO.setPopUrl("");
		jsonObject.put("results", new ArrayList(){{add(mhGeneralAppResultDataDTO);}});
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
		mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
		MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO mhGeneralAppResultDataField = MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.value(coverUrl)
				.flag("0")
				.build();
		mhGeneralAppResultDataFields.add(mhGeneralAppResultDataField);
		return RestRespDTO.success(jsonObject);
	}
	/**活动信息
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 19:02:58
	 * @param activityId
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/info")
	public RestRespDTO activityInfo(@PathVariable Integer activityId) {
		Activity activity = activityQueryService.getById(activityId);
		JSONObject jsonObject = new JSONObject();
		MhGeneralAppResultDataDTO mhGeneralAppResultDataDTO = new MhGeneralAppResultDataDTO();
		mhGeneralAppResultDataDTO.setType(3);
		mhGeneralAppResultDataDTO.setOrsUrl("");
		mhGeneralAppResultDataDTO.setPop(0);
		mhGeneralAppResultDataDTO.setPopUrl("");
		jsonObject.put("results", new ArrayList(){{add(mhGeneralAppResultDataDTO);}});
		List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
		mhGeneralAppResultDataDTO.setFields(mhGeneralAppResultDataFields);
		// 活动名称
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动名称")
				.value(activity.getName())
				.flag("1")
				.build());
		// 开始时间
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("时间")
				.value(DateTimeFormatterConstant.YYYY_MM_DD.format(activity.getStartDate()))
				.flag("100")
				.build());
		// 结束时间
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("时间")
				.value(DateTimeFormatterConstant.YYYY_MM_DD.format(activity.getEndDate()))
				.flag("101")
				.build());
		// 主办单位
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("主办单位")
				.value(activity.getCreateOrgName())
				.flag("102")
				.build());
		// 主办地点
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.key("活动地点")
				.value(activity.getAddress())
				.flag("103")
				.build());
		// 报名、签到人数
		SignParticipationDTO signParticipation = signApiService.getSignParticipation(activity.getSignId());
		StringBuilder signPepleNumDescribe = new StringBuilder();
		Integer limitNum = signParticipation.getLimitNum();
		Integer signedNum = signParticipation.getSignedNum();
		signPepleNumDescribe.append(signedNum);
		if (limitNum != null) {
			signPepleNumDescribe.append("/");
			signPepleNumDescribe.append(limitNum);
		}
		mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
				.value(signPepleNumDescribe.toString())
				.flag("104")
				.build());
		return RestRespDTO.success(jsonObject);
	}

	/**推荐活动
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-24 21:42:19
	 * @param activityId
	 * @param data
	 * @return com.chaoxing.activity.dto.RestRespDTO
	*/
	@RequestMapping("activity/{activityId}/recommend")
	public RestRespDTO recommendActivity(@PathVariable Integer activityId, @RequestBody String data) {
		JSONObject jsonObject = JSON.parseObject(data);
		Integer pageNum = jsonObject.getInteger("page");
		pageNum = Optional.ofNullable(pageNum).orElse(1);
		Integer pageSize = jsonObject.getInteger("pageSize");
		pageSize = Optional.ofNullable(pageSize).orElse(10);
		Activity activity = activityQueryService.getById(activityId);
		Integer createFid = activity.getCreateFid();
		// 查询机构下的活动列表
		Page<Activity> page = new Page(pageNum, pageSize);
		page = activityQueryService.listCreated(page, createFid);
		JSONObject result = new JSONObject();
		result.put("curPage", pageNum);
		result.put("totalPages", page.getPages());
		result.put("totalRecords", page.getTotal());
		List<Activity> records = page.getRecords();
		List<MhGeneralAppResultDataDTO> mhGeneralAppResultDatas = new ArrayList<>();
		jsonObject.put("results", mhGeneralAppResultDatas);
		if (CollectionUtils.isNotEmpty(records)) {
			for (Activity record : records) {
				MhGeneralAppResultDataDTO mhGeneralAppResultData = new MhGeneralAppResultDataDTO();
				mhGeneralAppResultData.setType(3);
				Integer pageId = record.getPageId();
				mhGeneralAppResultData.setOrsUrl(mhApiService.packageActivityAccessUrl(pageId));
				mhGeneralAppResultData.setPop(0);
				mhGeneralAppResultData.setPopUrl("");
				List<MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO> mhGeneralAppResultDataFields = new ArrayList<>();
				mhGeneralAppResultData.setFields(mhGeneralAppResultDataFields);
				// 封面
				mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
						.value(cloudApiService.getCloudImgUrl(record.getCoverCloudId()))
						.flag("0")
						.build());
				// 活动名称
				mhGeneralAppResultDataFields.add(MhGeneralAppResultDataDTO.MhGeneralAppResultDataFieldDTO.builder()
						.value(record.getName())
						.flag("1")
						.build());
				mhGeneralAppResultDatas.add(mhGeneralAppResultData);
			}
		}
		return RestRespDTO.success(result);
	}

}