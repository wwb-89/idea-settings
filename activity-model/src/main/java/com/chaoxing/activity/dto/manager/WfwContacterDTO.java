package com.chaoxing.activity.dto.manager;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**微服务联系人对象
 * @author wwb
 * @version ver 1.0
 * @className WfwContacterDTO
 * @description
 * @blame wwb
 * @date 2021-03-28 12:17:43
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WfwContacterDTO {

	private Integer puid;
	private Integer fid;
	private String uname;
	private String name;
	private String dept;
	private String phone;
	@JSONField(name = "deptids")
	private String deptIds;

}
