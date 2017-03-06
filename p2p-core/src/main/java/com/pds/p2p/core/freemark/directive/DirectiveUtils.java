package com.pds.p2p.core.freemark.directive;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModelException;

/**
 * @author badqiu
 */
public class DirectiveUtils {

    public static String BLOCK = "__ftl_override__";

    public static String getOverrideVariableName(String name) {
        return BLOCK + name;
    }

    public static void exposeRapidMacros(Configuration conf) {
        BlockDirective block = new BlockDirective();
        conf.setSharedVariable(block.getName(), block);
        ExtendsDirective extend = new ExtendsDirective();
        conf.setSharedVariable(extend.getName(), extend);
        OverrideDirective override = new OverrideDirective();
        conf.setSharedVariable(override.getName(), override);
        conf.setSharedVariable("setVariable", new SetVariableDirective());
    }

    @SuppressWarnings("rawtypes")
    static String getRequiredParam(Map params, String key) throws TemplateException {
        Object value = params.get(key);
        if (value == null || StringUtils.isEmpty(value.toString())) {
            throw new TemplateModelException("not found required parameter:" + key + " for directive");
        }
        return value.toString();
    }

    @SuppressWarnings("rawtypes")
    static String getParam(Map params, String key, String defaultValue) throws TemplateException {
        Object value = params.get(key);
        return value == null ? defaultValue : value.toString();
    }
}
