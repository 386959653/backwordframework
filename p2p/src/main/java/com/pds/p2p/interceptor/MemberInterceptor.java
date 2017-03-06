package com.pds.p2p.interceptor;

import com.pds.p2p.core.utils.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URLEncoder;

/**
 * Created by Wewon on 2017/3/5.
 */
public class MemberInterceptor implements HandlerInterceptor
{
    public final static String SEESION_MEMBER = "seesion_member";
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        //请求的路径
        String contextPath=httpServletRequest.getContextPath();
        String  url=httpServletRequest.getServletPath().toString();
        HttpSession session = httpServletRequest.getSession();
        String user = (String) session.getAttribute(SEESION_MEMBER);
        //这里可以根据session的用户来判断角色的权限，根据权限来重定向不同的页面，简单起见，这里只是做了一个重定向
        if (StringUtils.isEmpty(user)) {
            //被拦截，重定向到login界面
            httpServletResponse.sendRedirect(contextPath+"/login.htm?redirectURL="
                    + URLEncoder.encode(url));
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
