package com.pds.p2p.core.j2ee.tag;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

public class OverrideTag extends BodyTagSupport {
    private static final String CTX = "ctx";
    /**
     *
     */
    private static final long serialVersionUID = -3866858474407913572L;
    private String name;

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int doStartTag() throws JspException {
        return isOverrided() ? SKIP_BODY : EVAL_BODY_BUFFERED;
    }

    @Override
    public int doEndTag() throws JspException {
        if (isOverrided()) {
            return EVAL_PAGE;
        }
        BodyContent b = getBodyContent();
        if (b == null) {
            return EVAL_PAGE;
        }
        String varName = Utils.getOverrideVariableName(name);
        pageContext.setAttribute(varName, b.getString());
        pageContext.setAttribute(CTX, ((HttpServletRequest) pageContext.getRequest()).getContextPath());
        return EVAL_PAGE;
    }

    private boolean isOverrided() {
        String varName = Utils.getOverrideVariableName(name);
        return pageContext.getAttribute(varName) != null;
    }

}