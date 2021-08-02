package com.chaoxing.activity.dto.manager.sign.create;

import com.chaoxing.activity.dto.LoginUserDTO;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**创建报名签到的参数对象
 * @author wwb
 * @version ver 1.0
 * @className SignCreateParamDTO
 * @description 三方服务调用接口创建报名签到的参数对象
 * @blame wwb
 * @date 2021-07-12 14:44:50
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignCreateParamDTO {

	/** 报名签到id */
	private Integer id;
	/** 报名签到名称 */
	private String name;

	/** 报名列表 */
	private List<SignUpCreateParamDTO> signUps;
	/** 签到列表 */
	private List<SignInCreateParamDTO> signIns;

	/** 操作人uid */
	private Integer uid;
	/** 操作人姓名 */
	private String userName;
	/** 操作机构id */
	private Integer fid;
	/** 操作机构名 */
	private String orgName;

	public static SignCreateParamDTO buildDefault() {
		SignUpCreateParamDTO signUp = SignUpCreateParamDTO.buildDefault();
		SignInCreateParamDTO signIn = SignInCreateParamDTO.buildDefaultSignIn();
		SignInCreateParamDTO signOut = SignInCreateParamDTO.buildDefaultSignOut();
		return SignCreateParamDTO.builder()
				.signUps(Lists.newArrayList(signUp))
				.signIns(Lists.newArrayList(signIn, signOut))
				.build();
	}

	public void perfectCreator(LoginUserDTO loginUser) {
		setUid(loginUser.getUid());
		setUserName(loginUser.getRealName());
		setFid(loginUser.getFid());
		setOrgName(loginUser.getOrgName());
	}

	public void perfectName(String name) {
		setName(name);
	}

}