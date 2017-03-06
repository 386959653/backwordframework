package com.pds.p2p.core.freemark.directive;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <pre>
 * #首先，执行override建立环境内的变量，将指示内包含的内容放置一个变量内；
 * <@override name="body">
 *     <div class='content'>
 *         Powered By rapid-framework
 *     </div>
 * </@override>
 *
 * #然后，extends指示字将文件载入
 * <@extends name="base.flt"/>
 *
 * #最后，block指示字将在第一步建立的环境变量里取出内容，从而完成构建；
 * <!DOCTYPE html>
 * <html>
 *     <head>
 *         <@block name="head">base_head_content</@block>
 *     </head>
 *     <body>
 *         <@block name="body">base_body_content</@block>
 *     </body>
 * </html>
 *
 * </pre>
 *
 * @author badqiu
 */
public class OverrideDirective implements TemplateDirectiveModel {
    public String getName() {
        return "override";
    }

    @SuppressWarnings("rawtypes")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String name = DirectiveUtils.getRequiredParam(params, "name");
        String overrideVariableName = DirectiveUtils.getOverrideVariableName(name);

        if (env.getVariable(overrideVariableName) == null) {
            env.setVariable(overrideVariableName, new TemplateDirectiveBodyModel(body));
        }
    }

    public static class TemplateDirectiveBodyModel implements TemplateModel {
        public TemplateDirectiveBody body;

        public TemplateDirectiveBodyModel(TemplateDirectiveBody body) {
            this.body = body;
        }
    }
}
