package com.pds.p2p.core.data;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

public class FieldItem {
    private FieldDef def;
    private boolean editable;
    private String value;
    private String trns;

    public FieldItem(FieldDef def) {
        super();
        this.def = def;
        this.editable = true;
        this.trns = "N";
    }

    public int getLength() {
        return def.getLength();
    }

    public String getDlgType() {
        return def.getDlgType();
    }

    public String getLink() {
        return def.getLink();
    }

    public String getTrns() {
        return trns;
    }

    public void setTrns(String trns) {
        this.trns = trns;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getValue() {
        return StringUtils.defaultString(value);
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return def.getType();
    }

    public String name() {
        return def.name();
    }

    public String title() {
        return def.title();
    }

    public boolean hasTitle() {
        return def.hasTitle();
    }

    public List<Map<String, Object>> getListData() {
        return def.getListData();
    }

    public String listDataToJSON() {
        return def.listDataToJSON();
    }

    public String options() {
        return def.options();
    }

    public String optionsWithVal(String value) {
        return def.optionsWithVal(value);
    }

    public String optionText(String optId) {
        return def.optionText(optId);
    }

    public String checked(String value) {
        return def.checked(value);
    }

    public String field() {
        return def.field();
    }

    public List<String> getList() {
        return def.getList();
    }

    public boolean isEmpty() {
        return def.isRequired();
    }

    public boolean isHide() {
        return def.isHide();
    }

    public String id() {
        return def.id();
    }

    public FieldDef getDef() {
        return def;
    }

}
