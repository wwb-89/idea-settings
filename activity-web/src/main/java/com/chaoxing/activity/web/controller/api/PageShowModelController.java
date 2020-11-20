package com.chaoxing.activity.web.controller.api;


import com.chaoxing.activity.dto.pageShowModel;
import com.chaoxing.activity.service.activity.module.ActivityModuleService;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    @GetMapping("/msg/{current}/{limit}")
    @ResponseBody
    public List<pageShowModel> showPageMsg( Model model, HttpServletRequest request,@PathVariable("current") Integer current,@PathVariable("limit") Integer limit){
        List<pageShowModel> list  = service.getModelMsgPage(current,limit);
        model.addAttribute("list",list);

        return list;
    }
}
