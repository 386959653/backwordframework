package com.pds.p2p.controller;

import com.pds.p2p.core.j2ee.action.JsonResult;
import com.pds.p2p.core.j2ee.web.BaseController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by Wewon on 2017/2/19.
 */
@Controller
@RequestMapping("cat")
public class Test extends BaseController {
    @ResponseBody
    @RequestMapping("save")
    public JsonResult<?> saveList() {
        return JsonResult.okJsonResult();
    }
}
