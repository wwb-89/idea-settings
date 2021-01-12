package com.chaoxing.activity.admin.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author wwb
 * @version ver 1.0
 * @className TestController
 * @description
 * @blame wwb
 * @date 2021-01-12 10:13:52
 */
@Controller
@RequestMapping("test")
public class TestController {

	@RequestMapping("")
	public String a() {
		return "test/test";
	}

}