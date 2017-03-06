package com.pds.p2p.system.i18n;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pds.p2p.system.web.session.SessionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import com.pds.p2p.system.redis.RedisUtil;
import com.pds.p2p.system.web.session.SessionUser;

public class RedisLocaleChangeInterceptor extends HandlerInterceptorAdapter {

    private static Logger logger = LogManager.getLogger(RedisLocaleChangeInterceptor.class);

    public static final String DEFAULT_PARAM_NAME = "locale";

    private String paramName = DEFAULT_PARAM_NAME;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {

        String newLocale = request.getParameter(getParamName());

        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);

        if (newLocale != null) { // 如果request 带 locale 参数
            if (localeResolver == null) {
                throw new IllegalStateException(
                        "No LocaleResolver found: not in a DispatcherServlet request?");
            }

            try {

                Locale locale = StringUtils.parseLocaleString(newLocale);
                LangEnv.setLang(locale);

                if (!org.apache.commons.lang3.StringUtils.equalsIgnoreCase(LangSuport.EN_US.getLang(), newLocale)
                        && !org.apache.commons.lang3.StringUtils
                        .equalsIgnoreCase(LangSuport.ZH_CN.getLang(), newLocale)) {

                    locale = localeResolver.resolveLocale(request);

                    LangEnv.setLang(locale);

                    logger.debug(String.format("error locale string [%s], change locale to current [%s]", newLocale,
                            locale.toString()));

                } else {

                    localeResolver.setLocale(request, response, locale);

                    SessionUser user = SessionUtils.getSessionUser();

                    if (!StringUtils.isEmpty(user)) { // 判断是否有用户  无需权限拦截的路径 没有用户

                        RedisUtil.setLocale(user.getUsername(), locale);

                        logger.debug(String.format("user [%s] change locale to [%s]", user.getUsername(),
                                locale.toString()));
                    }

                }

            } catch (IllegalArgumentException ex) {
                logger.debug("invalid locale value [" + newLocale + "]: " + ex.getMessage());
            }

        } else {// 如果request 不带 locale 参数

            SessionUser user = SessionUtils.getSessionUser();

            Locale oldLocale = localeResolver.resolveLocale(request);

            if (!StringUtils.isEmpty(user)) { // 如果有用户信息

                Locale locale = RedisUtil.getLocale(user.getUsername()); // 从redis获取locale

                if (locale != null) { // 用户locale不为空，并且 和 cookie缓存不一致  更新 cookie缓存

                    if (!StringUtils.endsWithIgnoreCase(locale.toString(), oldLocale.toString())) {

                        localeResolver.setLocale(request, response, locale);

                    }
                    LangEnv.setLang(locale);

                } else { // 用户locale为空，设置用户为 默认值

                    RedisUtil.setLocale(user.getUsername(), oldLocale);
                    LangEnv.setLang(oldLocale);

                    logger.debug(
                            String.format("user [%s] set locale to [%s]", user.getUsername(), oldLocale.toString()));
                }

            } else {
                LangEnv.setLang(oldLocale);
            }
        }

        return true;
    }

    public String getParamName() {
        return paramName;
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

}
