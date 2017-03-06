package com.pds.p2p.core.freemark.directive;

import java.io.IOException;
import java.util.Map;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateDirectiveModel;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * @author badqiu
 */
public class BlockDirective implements TemplateDirectiveModel {

    public String getName() {
        return "block";
    }

    /**
     * params:指示字里的参数 loopVars：指示字内的循环变量 body：指示字内所包含的内容
     */
    @SuppressWarnings("rawtypes")
    public void execute(Environment env, Map params, TemplateModel[] loopVars, TemplateDirectiveBody body)
            throws TemplateException, IOException {
        String name = DirectiveUtils.getRequiredParam(params, "name");
        OverrideDirective.TemplateDirectiveBodyModel overrideBody = getOverrideBody(env, name);
        TemplateDirectiveBody outputBody = overrideBody == null ? body : overrideBody.body;
        if (outputBody != null) {
            outputBody.render(env.getOut());
        }
    }

    private OverrideDirective.TemplateDirectiveBodyModel getOverrideBody(Environment env, String name)
            throws TemplateModelException {
        OverrideDirective.TemplateDirectiveBodyModel value = (OverrideDirective.TemplateDirectiveBodyModel) env
                .getVariable(DirectiveUtils.getOverrideVariableName(name));
        return value;
    }

}
