package com.pds.p2p.core.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FieldDef {
    private static final String ID = "id";
    public static final String TEXT = "text";
    public static final String SELECT = "select";
    public static final String DATE = "date";
    public static final String NUMBER = "number";
    public static final String FORMULA = "formula";
    public static final String ENMU = "enmu";
    public static final String GET = "get";

    public enum FieldType {
        ID, TEXT, SELECT, DATE, NUMBER, FORMULA
    }

    ;

    private String id; //
    private String name; // 名称,一般是汉字
    private String field; // 对应数据的field
    private String title;
    private String link;
    private String dlgType;
    private boolean hide;
    private boolean required;
    private String type = TEXT;
    private List<String> list;
    private List<Map<String, Object>> listData;
    private Integer length;
    private String format;
    private Integer scale;
    private String formula;
    private String computeLevel;
    private boolean unique;
    private int maxlength;
    private int minlength;
    private int max;
    private int min;
    private boolean digits;
    private String validation;
    private String dataTextField = ID;
    private String dataValueField = TEXT;

    public boolean isDigits() {
        return digits;
    }

    public void setDigits(boolean digits) {
        this.digits = digits;
    }

    public int getMinlength() {
        return minlength;
    }

    public void setMinlength(int minlength) {
        this.minlength = minlength;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public void setMin(int min) {
        this.min = min;
    }

    public int getMaxlength() {
        return maxlength;
    }

    public void setMaxlength(int maxlength) {
        this.maxlength = maxlength;
    }

    public String getValidation() {
        return validation;
    }

    public String getDataTextField() {
        return dataTextField;
    }

    public void setDataTextField(String dataTextField) {
        this.dataTextField = dataTextField;
    }

    public String getDataValueField() {
        return dataValueField;
    }

    public void setDataValueField(String dataValueField) {
        this.dataValueField = dataValueField;
    }

    public void setValidation(String validation) {
        this.validation = StringUtils.trimToEmpty(validation).toLowerCase();
        String rules[] = StringUtils.split(this.validation);
        for (String rule : rules) {
            if (StringUtils.startsWith(rule, "required")) {
                this.setRequired(true);
            } else if (StringUtils.startsWith(rule, "unique")) {
                this.setUnique(true);
            } else if (StringUtils.startsWith(rule, "digits")) {
                this.setDigits(true);
            } else if (StringUtils.startsWith(rule, "maxlength")) {
                String ss[] = StringUtils.split(rule, ':');
                this.setMaxlength(NumberUtils.toInt(ss[1]));
            } else if (StringUtils.startsWith(rule, "precision")) {
                String ss[] = StringUtils.split(rule, ':');
                this.setScale(NumberUtils.toInt(ss[1]));
            }

        }

    }

    public boolean isUnique() {
        return unique;
    }

    public void setUnique(boolean unique) {
        this.unique = unique;
    }

    public String getComputeLevel() {
        return computeLevel;
    }

    public void setComputeLevel(String computeLevel) {
        this.computeLevel = computeLevel;
    }

    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public Integer getScale() {
        return scale;
    }

    public void setScale(Integer scale) {
        this.scale = scale;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String dateFmt) {
        this.format = dateFmt;
    }

    public Integer getLength() {
        return length;
    }

    public void setLength(Integer length) {
        this.length = length;
    }

    public String getDlgType() {
        return dlgType;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getField() {
        return StringUtils.defaultString(field, id);
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDlgType(String dlgType) {
        this.dlgType = dlgType;
    }

    public String getLink() {
        return StringUtils.defaultString(link);
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isRequired() {
        return required;
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    public boolean isHide() {
        return hide;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public FieldDef() {
    }

    public FieldDef(ListData listData) {
        this.id = listData.getName();
        this.setListData(listData.toList());
    }

    public FieldDef(String id) {
        this.id = id;
    }

    public FieldDef(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public FieldDef(String id, String name, String field) {
        this.id = id;
        this.name = name;
        this.field = field;
    }

    public FieldDef id(String id) {
        this.id = id;
        return this;
    }

    public FieldDef name(String name) {
        this.name = name;
        return this;
    }

    public FieldDef field(String field) {
        this.field = field;
        return this;
    }

    public FieldDef title(String title) {
        this.title = title;
        return this;
    }

    public String id() {
        return this.id;
    }

    public String name() {
        return StringUtils.defaultIfEmpty(name, this.id);
    }

    public String field() {
        return StringUtils.defaultIfEmpty(field, this.id);
    }

    public String title() {
        return StringUtils.defaultIfEmpty(title, this.name);
    }

    public boolean hasTitle() {
        return StringUtils.isEmpty(title);
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public List<Map<String, Object>> getListData() {
        return listData;
    }

    public void setListData(List<Map<String, Object>> listData) {
        this.listData = listData;
    }

    public FieldDef fromListData(ListData listData) {
        this.setListData(listData.toList());
        this.setType("select");
        return this;
    }

    public FieldDef li(String id, String text) {
        if (this.listData == null) {
            this.listData = new ArrayList<Map<String, Object>>(5);
        }
        this.listData.add(ImmutableMap.<String, Object>of(
                this.dataValueField, id,
                this.dataTextField, text));
        return this;
    }

    public String listDataToJSON() {
        JSONObject jsonObject = new JSONObject();
        for (Map<String, Object> map : listData) {
            jsonObject.put(map.get(this.dataValueField).toString(), map.get(this.dataTextField));
        }
        return jsonObject.toJSONString();
    }

    public Map<String, Object> listDataToMap() {
        Map<String, Object> result = Maps.newLinkedHashMap();
        for (Map<String, Object> map : listData) {
            result.put(map.get(this.dataValueField).toString(), map.get(this.dataTextField));
        }
        return result;
    }

    public String options() {
        if (this.listData == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : listData) {
            sb.append("<option value='").append(map.get(dataValueField)).append("'>");
            sb.append(map.get(this.dataTextField)).append("</option>");
        }
        return sb.toString();
    }

    public String optionsWithVal(String value) {
        if (this.listData == null) {
            return StringUtils.EMPTY;
        }
        StringBuilder sb = new StringBuilder();
        for (Map<String, Object> map : listData) {
            sb.append("<option value='").append(map.get(dataValueField));
            if (StringUtils.equals(value, map.get(dataValueField).toString())) {
                sb.append("' selected=\'selected\'>");
            } else {
                sb.append("'>");
            }
            sb.append(map.get(dataTextField)).append("</option>");
        }
        return sb.toString();
    }

    public String optionText(String optId) {
        if (this.listData == null) {
            return StringUtils.EMPTY;
        }
        for (Map<String, Object> map : listData) {
            if (StringUtils.equals(optId, map.get(dataValueField).toString())) {
                return map.get(dataTextField).toString();
            }
        }
        return StringUtils.EMPTY;
    }

    public String optionId(String optText) {
        if (this.listData == null) {
            return StringUtils.EMPTY;
        }
        for (Map<String, Object> map : listData) {
            if (StringUtils.equals(optText, map.get(dataTextField).toString())) {
                return map.get(dataValueField).toString();
            }
        }
        return StringUtils.EMPTY;
    }

    public void setOptionText(String optId, String optText) {
        if (this.listData == null) {
            return;
        }
        for (Map<String, Object> map : listData) {
            if (StringUtils.equals(optId, map.get(dataValueField).toString())) {
                map.put(dataTextField, optText);
            }
        }
    }

    // listData的别名
    public void setOptions(List<Map<String, Object>> options) {
        this.listData = options;
    }

    public String checked(String value) {
        if (this.listData == null) {
            return StringUtils.EMPTY;
        }
        for (Map<String, Object> map : listData) {
            if (StringUtils.equals(value, map.get(dataValueField).toString())) {
                return "checked";
            }
        }
        return StringUtils.EMPTY;
    }

    public void listDataRetain(String... ids) {
        if (this.listData == null) {
            return;
        }
        if (ids.length == listData.size()) {
            return;
        }
        Iterator<Map<String, Object>> it = this.listData.iterator();
        while (it.hasNext()) {
            Map<String, Object> map = it.next();
            String id = (String) map.get(dataValueField);
            if (!ArrayUtils.contains(ids, id)) {
                it.remove();
            }
        }
    }

    public FieldDef clone() {
        FieldDef fieldDef = new FieldDef(this.id, this.name);
        fieldDef.field(this.field);
        fieldDef.title(this.title);
        fieldDef.setRequired(this.isRequired());
        fieldDef.setType(this.getType());
        fieldDef.setHide(this.isHide());
        fieldDef.setLink(this.getLink());
        @SuppressWarnings("unchecked")
        List<String> newList = this.list != null ? Lists.newArrayList(this.list) : Collections.EMPTY_LIST;
        fieldDef.setList(newList);
        List<Map<String, Object>> opts = getListData();
        if (opts != null) {
            List<Map<String, Object>> newopts = Lists.newArrayListWithCapacity(opts.size());
            for (Map<String, Object> map : opts) {
                newopts.add(ImmutableMap.<String, Object>of(ID, map.get(ID), "text", map.get(TEXT)));
            }
            fieldDef.setListData(newopts);
        }
        return fieldDef;
    }

    public String bulidValidateJS() {
        Validity validity = new Validity("#" + this.id());
        if (!isRequired()) {
            validity.require(String.format("%s需要填写.", title()));
        }
        if (getType().equalsIgnoreCase("number")) {
            validity.match("number", String.format("%s需要填写数字.", title()));
        } else {
            return StringUtils.EMPTY;
        }
        return validity.toString();
    }

    public boolean isTextType() {
        return StringUtils.equalsIgnoreCase("text", this.getType());
    }

    public boolean isGetType() {
        return StringUtils.equalsIgnoreCase("get", this.getType());
    }

    public boolean isGetCellType() {
        return StringUtils.equalsIgnoreCase("get_cell", this.getType());
    }

    public boolean isGetRowType() {
        return StringUtils.equalsIgnoreCase("get_row", this.getType());
    }

    public boolean isCalcType() {
        return StringUtils.equalsIgnoreCase("calc", this.getType());
    }

    public boolean isFormulaType() {
        return StringUtils.equalsIgnoreCase("formula", this.getType());
    }

    public boolean isDateType() {
        return StringUtils.equalsIgnoreCase("date", this.getType());
    }

    public boolean isEnumType() {
        return StringUtils.equalsIgnoreCase("enum", getType()) || StringUtils.equalsIgnoreCase("select", getType());
    }

    public boolean isNumberType() {
        return StringUtils.equalsIgnoreCase("number", getType()) || StringUtils.equalsIgnoreCase("int", getType())
                || StringUtils.equalsIgnoreCase("float", getType()) || StringUtils
                .equalsIgnoreCase("double", getType());
    }

    @Override
    public String toString() {
        return "FieldDef [id=" + id + ", name=" + name + ", field=" + field + ", title=" + title + ", hide=" + hide
                + ", empty=" + required + ", type=" + type + ", listData=" + listData + "]";
    }
}
