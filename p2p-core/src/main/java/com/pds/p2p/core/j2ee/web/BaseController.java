package com.pds.p2p.core.j2ee.web;

import java.beans.PropertyEditorSupport;
import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pds.p2p.core.j2ee.action.JsonResult;
import com.pds.p2p.core.utils.Exceptions;
import com.pds.p2p.system.config.ConfigConstants;
import com.pds.p2p.system.log.LogBeanHolder;
import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.alibaba.fastjson.JSON;
import com.pds.p2p.core.utils.DateUtils;
import com.pds.p2p.core.utils.StringUtils;
import com.pds.p2p.system.log.bean.BizLogBean;

/**
 * 控制器支持类
 */
public abstract class BaseController {

    /**
     * 日志对象
     */
    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String Page_500 = ConfigConstants.ERROR_500;

    private String viewPrefix;

    private String basePath;

    protected BaseController() {
        setViewPrefix(defaultViewPrefix());
    }

    protected BaseController(String basePath) {
        super();
        this.basePath = basePath;
    }

    protected String view(String viewName) {
        return String.format("%s/%s", this.basePath, viewName);
    }

    protected String defaultViewPrefix() {
        String currentViewPrefix = "";
        RequestMapping requestMapping = AnnotationUtils.findAnnotation(getClass(), RequestMapping.class);
        if (requestMapping != null && requestMapping.value().length > 0) {
            currentViewPrefix = requestMapping.value()[0];
        }
        return currentViewPrefix;
    }

    /**
     * 当前模块 视图的前缀 默认 1、获取当前类头上的@RequestMapping中的value作为前缀 2、如果没有就使用当前模型小写的简单类名
     */
    public void setViewPrefix(String viewPrefix) {
        if (viewPrefix.startsWith("/")) {
            viewPrefix = viewPrefix.substring(1);
        }
        this.viewPrefix = viewPrefix;
    }

    public String getViewPrefix() {
        return viewPrefix;
    }

    /**
     * 添加Model消息
     */
    protected void addMessage(Model model, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        model.addAttribute("message", sb.toString());
    }

    /**
     * 添加Flash消息
     */
    protected void addMessage(RedirectAttributes redirectAttributes, String... messages) {
        StringBuilder sb = new StringBuilder();
        for (String message : messages) {
            sb.append(message).append(messages.length > 1 ? "<br/>" : "");
        }
        redirectAttributes.addFlashAttribute("message", sb.toString());
    }

    /**
     * 客户端返回JSON字符串
     *
     * @param response
     * @param object
     */
    protected void renderString(HttpServletResponse response, Object object) {
        renderString(response, JSON.toJSONString(object), "application/json");
    }

    protected void renderJsonResult(HttpServletResponse response, JsonResult<?> jsonResult) {
        renderString(response, jsonResult.toJSONString(), "application/json");
    }

    /**
     * 客户端返回字符串
     *
     * @param response
     * @param string
     *
     * @return
     */
    protected void renderString(HttpServletResponse response, String string, String type) {
        try {
            response.reset();
            response.setContentType(type);
            response.setCharacterEncoding("utf-8");
            response.getWriter().print(string);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param backURL null 将重定向到默认getViewPrefix()
     *
     * @return
     */
    protected String redirectToUrl(String backURL) {
        if (StringUtils.isEmpty(backURL)) {
            backURL = getViewPrefix();
        }
        if (!backURL.startsWith("/") && !backURL.startsWith("http")) {
            backURL = "/" + backURL;
        }
        return "redirect:" + backURL;
    }

    /**
     * 初始化数据绑定 1. 将所有传递进来的String进行HTML编码，防止XSS攻击 2. 将字段中Date类型转换为String类型
     */
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        // String类型转换，将所有传递进来的String进行HTML编码，防止XSS攻击
        binder.registerCustomEditor(String.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                setValue(text == null ? null : StringEscapeUtils.escapeHtml4(text.trim()));
            }

            @Override
            public String getAsText() {
                Object value = getValue();
                return value != null ? value.toString() : "";
            }
        });
        // Date 类型转换
        binder.registerCustomEditor(Date.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                try {
                    setValue(DateUtils.parseDate(text));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletRequest request, HttpServletResponse response) {
        ex.printStackTrace();

        BizLogBean bizLog = LogBeanHolder.getBizLogBean();
        bizLog.setLevel("1");
        bizLog.setContent(Exceptions.getStackTraceAsString(ex));
        bizLog.setExt1("The exception class:" + ex.getClass().getName());
        bizLog.setExt2("The exception message:" + ex.getMessage());
        bizLog.setExt3("The exception case:" + ex.getCause());
        bizLog.setExt4("User-Agent:" + request.getHeader("User-Agent"));

        // 如果是异步请求或是手机端，则直接返回信息
        if (Servlets.isAjaxRequest(request)) {
            JsonResult<?> jsonResult = JsonResult.failJsonResult(ex);
            this.renderJsonResult(response, jsonResult);
        } else {
            try {
                request.setAttribute("exception", ex);
                request.getRequestDispatcher(Page_500).forward(request, response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
