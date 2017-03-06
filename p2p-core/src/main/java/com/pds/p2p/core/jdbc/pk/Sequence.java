package com.pds.p2p.core.jdbc.pk;

public class Sequence {
    private static final int INCREMENT = 5;

    private String name;
    private long nextid;
    private int increment;
    private long currid;
    private int padding;
    private String prefix;

    public Sequence() {
        increment = INCREMENT;
        this.padding = 9;
        this.prefix = "";
    }

    public Sequence(int increment) {
        this.increment = increment;
        this.padding = 9;
        this.prefix = "";
    }

    public void incCurrid() {
        ++currid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getNextid() {
        return nextid;
    }

    public void setNextid(long nextid) {
        this.nextid = nextid;
    }

    public int getIncrement() {
        return increment;
    }

    public void setIncrement(int increment) {
        this.increment = increment;
    }

    public long getCurrid() {
        return currid;
    }

    public void setCurrid(long currid) {
        this.currid = currid;
    }

    public int getPadding() {
        return padding;
    }

    public void setPadding(int padding) {
        this.padding = padding;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
