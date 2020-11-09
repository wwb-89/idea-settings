package com.chaoxing.activity.dto;

import com.chaoxing.activity.util.enums.StatusCodeEnum;
import com.chaoxing.activity.util.exception.BusinessException;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**json响应对象
 * @author wwb
 * @version ver 1.0
 * @className RestRespDTO
 * @description
 * @blame wwb
 * @date 2019-10-22 15:20:18
 */
@Data
@Builder
public class RestRespDTO<T> {

	private Boolean success;
	private Integer code;
	private String message;

	private T data;

	private String timestamp;

	public static RestRespDTO success() {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.SUCCESS.isSuccess())
				.code(StatusCodeEnum.SUCCESS.getCode())
				.message(StatusCodeEnum.SUCCESS.getMessage())
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static <T> RestRespDTO success(T data) {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.SUCCESS.isSuccess())
				.code(StatusCodeEnum.SUCCESS.getCode())
				.message(StatusCodeEnum.SUCCESS.getMessage())
				.data(data)
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static <T> RestRespDTO success(List<T> datas) {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.SUCCESS.isSuccess())
				.code(StatusCodeEnum.SUCCESS.getCode())
				.message(StatusCodeEnum.SUCCESS.getMessage())
				.data(datas)
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static RestRespDTO fail() {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.FAIL.isSuccess())
				.code(StatusCodeEnum.FAIL.getCode())
				.message(StatusCodeEnum.FAIL.getMessage())
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static RestRespDTO fail(String message) {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.FAIL.isSuccess())
				.code(StatusCodeEnum.FAIL.getCode())
				.message(message)
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static RestRespDTO error() {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.ERROR.isSuccess())
				.code(StatusCodeEnum.ERROR.getCode())
				.message(StatusCodeEnum.ERROR.getMessage())
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static RestRespDTO error(String message) {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.ERROR.isSuccess())
				.code(StatusCodeEnum.ERROR.getCode())
				.message(message)
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

	public static RestRespDTO error(BusinessException e) {
		return RestRespDTO.builder()
				.success(StatusCodeEnum.ERROR.isSuccess())
				.code(StatusCodeEnum.ERROR.getCode())
				.message(e.getMessage())
				.timestamp(String.valueOf(System.currentTimeMillis()))
				.build();
	}

}
