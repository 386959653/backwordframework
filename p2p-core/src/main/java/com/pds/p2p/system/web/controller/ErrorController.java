package com.pds.p2p.system.web.controller;

import com.pds.p2p.system.config.ConfigConstants;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pds.p2p.core.j2ee.web.BaseController;

@Controller
@RequestMapping("error")
public class ErrorController extends BaseController {

    @RequestMapping("500")
    public String to500() {

        return ConfigConstants.ERROR_500;
    }

    @RequestMapping("404")
    public String to404() {

        return ConfigConstants.ERROR_404;
    }

    @RequestMapping("403")
    public String to403() {

        return ConfigConstants.ERROR_403;
    }

}
