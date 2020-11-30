package com.chaoxing.activity.admin.controller.api;


import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;

/** 活动管理
 * @author dkm
 * @version ver 1.0
 * @className PageShowModelController
 * @description 活动展示页面
 * @blame dkm
 * @date 2020-11-19 14：39：20
 */

@Controller
@RequestMapping("/pageshow")
@CrossOrigin
public class PageShowModelController {

    @Autowired
    private ActivityModuleService service;

    /*
    * author dkm
    * description 展示所有的活动页面信息,按分页展示
    *
    * */

}
