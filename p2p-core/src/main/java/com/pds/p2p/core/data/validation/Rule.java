package com.pds.p2p.core.data.validation;

public class Rule {
    public Rule(String method, Object parameters) {
        super();
        this.method = method;
        this.parameters = parameters;
    }

    public Rule(String method, Object parameters, String message) {
        super();
        this.method = method;
        this.parameters = parameters;
        this.message = message;
    }

    private String method;
    private Object parameters;
    private String message;
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getParameters() {
        return parameters;
    }

    public void setParameters(Object parameters) {
        this.parameters = parameters;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
