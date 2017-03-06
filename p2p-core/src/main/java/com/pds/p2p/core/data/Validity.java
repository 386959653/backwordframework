package com.pds.p2p.core.data;

import org.apache.commons.lang3.StringEscapeUtils;

public class Validity {
    public static void main(String[] args) {
        System.out.println(new Validity("#delete").require().toString());
    }

    private StringBuilder sb;

    public Validity(String selector) {
        sb = new StringBuilder();
        sb.append("$('");
        sb.append(selector);
        sb.append("')");
    }

    public Validity require() {
        sb.append(".require()");
        return this;
    }

    public Validity require(String msg) {
        sb.append(".require('").append(StringEscapeUtils.escapeHtml4(msg)).append("')");
        return this;
    }

    public Validity match(String patten) {
        sb.append(".match('").append(patten).append("')");
        return this;
    }

    public Validity match(String patten, String msg) {
        sb.append(".match('").append(patten).append("','").append(msg).append("')");
        return this;
    }

    public Validity number() {
        return this.match("number");
    }

    public String toString() {
        return sb.append(";").toString();
    }

}
