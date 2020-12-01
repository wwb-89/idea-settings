package com.chaoxing.activity.admin.util;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

/**http请求工具类
 * @author wwb
 * @version ver 1.0
 * @className HttpServletRequestUtils
 * @description
 * @blame wwb
 * @date 2020-11-13 14:26:51
 */
public class HttpServletRequestUtils {

	private static final String PAGENUM_PARAM_KEY = "pageNum";
	private static final String PAGESIZE_PARAM_KEY = "pageSize";

	private static final Integer DEFAULT_PAGE_NUM = 1;
	private static final Integer DEFAULT_PAGE_SIZE = 10;

	private HttpServletRequestUtils() {

	}

	/**构建分页对象
	 * @Description 
	 * @author wwb
	 * @Date 2020-11-13 14:31:13
	 * @param request
	 * @return com.baomidou.mybatisplus.extension.plugins.pagination.Page
	*/
	public static <T> Page<T> buid(HttpServletRequest request) {
		Integer pageNum = DEFAULT_PAGE_NUM;
		Integer pageSize = DEFAULT_PAGE_SIZE;

		String pageNumStr = request.getParameter(PAGENUM_PARAM_KEY);
		String pageSizeStr = request.getParameter(PAGESIZE_PARAM_KEY);

		if (StringUtils.isNotBlank(pageNumStr)) {
			try {
				pageNum = Integer.parseInt(pageNumStr);
			} catch (NumberFormatException e) {

			}
		}
		if (StringUtils.isNotBlank(pageSizeStr)) {
			try {
				pageSize = Integer.parseInt(pageSizeStr);
			} catch (NumberFormatException e) {

			}
		}
		Page<T> page = new Page(pageNum, pageSize);
		return page;
	}

}
