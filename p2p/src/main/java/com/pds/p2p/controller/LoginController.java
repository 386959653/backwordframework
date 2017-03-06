package com.pds.p2p.controller;

import com.pds.p2p.core.utils.StringUtils;
import com.pds.p2p.interceptor.MemberInterceptor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.net.URLDecoder;

/**
 * Created by Wewon on 2017/3/5.
 */
@Controller
@RequestMapping("login")
public class LoginController {
    @RequestMapping(value = "/submit", method = RequestMethod.POST)
    public String submit(String username, String password, String redirectURL,
                         HttpServletRequest request) {
        //模拟登陆成功 用户admin 密码admin的用户
        if (StringUtils.isNotBlank(username) && StringUtils.isNotBlank(password)
                && username.equals("admin") && password.equals("admin")) {
            //当登陆成功是，将用户信息存放到session中去
            HttpSession session = request.getSession();
            session.setAttribute(MemberInterceptor.SEESION_MEMBER, "admin");
            if (StringUtils.isNotBlank(redirectURL)) {
                return "redirect:" + URLDecoder.decode(redirectURL);
            }
//            return "redirect:/member/index.htm";
            return "redirect:/cat/save";
        } else {
            if (StringUtils.isNotBlank(redirectURL)) {
                return "redirect:/login.htm?" + URLDecoder.decode(redirectURL);
            }
            return "redirect:/login.htm";
        }
    }
}
