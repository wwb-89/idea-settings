package com.chaoxing.activity.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**机构地址
 * @author wwb
 * @version ver 1.0
 * @className OrgAddressDTO
 * @description
 * @blame wwb
 * @date 2020-11-10 14:26:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrgAddressDTO {

	/** fid */
	private Integer fid;
	/** 机构名称 */
	private String orgName;
	/** 国家 */
	private String country;
	/** 省 */
	private String province;
	/** 市 */
	private String city;
	/** 区县 */
	private String county;

}
