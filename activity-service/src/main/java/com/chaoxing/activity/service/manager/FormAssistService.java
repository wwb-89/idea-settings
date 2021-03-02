package com.chaoxing.activity.service.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.chaoxing.activity.model.Activity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**表单辅助服务
 * @author wwb
 * @version ver 1.0
 * @className FormAssistService
 * @description
 * @blame wwb
 * @date 2021-02-06 19:18:59
 */
@Slf4j
@Service
public class FormAssistService {

	/**获取活动填写表单数据
	 * @Description 
	 * @author wwb
	 * @Date 2021-02-06 19:41:20
	 * @param formInfoData
	 * @param activity
	 * @return java.lang.String
	*/
	public String getActivityFillFormData(String formInfoData, Activity activity) {
		JSONArray result = new JSONArray();
		JSONArray jsonArray = JSON.parseArray(formInfoData);
		int size = jsonArray.size();
		for (int i = 0; i < size; i++) {
			JSONObject jsonObject = jsonArray.getJSONObject(i);
			String label = jsonObject.getString("label");
			JSONObject item = new JSONObject();
			item.put("compt", jsonObject.getString("compt"));
			item.put("comptId", jsonObject.getString("id"));
			JSONArray data = new JSONArray();
			result.add(item);
			if ("活动ID".equals(label)) {
				data.add(activity.getId());
				item.put("val", data);
				continue;
			}
			if ("活动名称或内容".equals(label)) {
				data.add(activity.getName());
				item.put("val", data);
				continue;
			}
			if ("待审核数量".equals(label)) {
				item.put("val", new JSONArray());
				continue;
			}
			if ("参与学院".equals(label)) {
				data.add("信息工程学院");
				item.put("val", data);
				continue;
			}
			if ("参与年级".equals(label)) {
				data.add("2018,2019,2020,2021");
				item.put("val", data);
				continue;
			}
			if ("课程项目类型".equals(label)) {
				data.add(activity.getActivityType());
				item.put("val", data);
				continue;
			}
			if ("学分类型编号".equals(label)) {
				item.put("val",new JSONArray());
				continue;
			}
			if ("活动积分".equals(label)) {
				data.add(2);
				item.put("val", data);
				continue;
			}
			if ("单位".equals(label)) {
				data.add("积分");
				item.put("val", data);
				continue;
			}
			if ("活动预览".equals(label)) {
				data.add(activity.getPreviewUrl());
				item.put("val", data);
				continue;
			}
			if ("发起人".equals(label)) {
				data.add(activity.getCreateUserName());
				item.put("val", data);
				continue;
			}
			if ("组织者".equals(label)) {
				JSONObject user = new JSONObject();
				user.put("puid", activity.getCreateUid());
				user.put("uname", activity.getCreateUserName());
				data.add(user);
				item.put("val", data);
				continue;
			}
			if ("活动状态".equals(label)) {
				Integer status = activity.getStatus();
				Activity.StatusEnum statusEnum = Activity.StatusEnum.fromValue(status);
				data.add(statusEnum.getName());
				item.put("val", data);
				continue;
			}
		}
		return result.toJSONString();
	}

}