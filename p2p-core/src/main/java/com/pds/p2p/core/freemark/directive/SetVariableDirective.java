package com.pds.p2p.core.freemark.directive;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.ext.servlet.AllHttpScopesHashModel;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

public class SetVariableDirective implements TemplateDirectiveModel {
    private static Log log = LogFactory.getLog(SetVariableDirective.class);

    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String name = DirectiveUtils.getRequiredParam(params, "name");
        try {
            Field field = env.getClass().getDeclaredField("rootDataModel");
            field.setAccessible(true);  //设置私有属性范围
            AllHttpScopesHashModel rootDataModel = (AllHttpScopesHashModel) field.get(env);
            StringWriter writer = new StringWriter();
            body.render(writer);
            rootDataModel.put(name, writer.toString());
            log.debug(writer.toString());
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
