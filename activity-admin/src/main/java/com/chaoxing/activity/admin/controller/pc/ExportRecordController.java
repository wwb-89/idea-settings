package com.chaoxing.activity.admin.controller.pc;

import com.chaoxing.activity.admin.util.LoginUtils;
import com.chaoxing.activity.dto.LoginUserDTO;
import com.chaoxing.activity.model.ExportRecord;
import com.chaoxing.activity.service.export.ExportRecordQueryService;
import com.chaoxing.activity.util.annotation.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author huxiaolong
 * <p>
 * @version 1.0
 * @date 2021/6/1 4:33 下午
 * <p>
 */
@Controller
@RequestMapping("export-record")
public class ExportRecordController {

    @Resource
    private ExportRecordQueryService exportRecordQueryService;

    @LoginRequired
    @RequestMapping("")
    public String activityStatIndex(HttpServletRequest request, Model model, String exportType) {
        LoginUserDTO loginUser = LoginUtils.getLoginUser(request);
        List<ExportRecord> recordList = exportRecordQueryService.listRecord(loginUser.getFid(), exportType);
        model.addAttribute("exportRecordData", recordList);
        return "pc/export/index";
    }
}
