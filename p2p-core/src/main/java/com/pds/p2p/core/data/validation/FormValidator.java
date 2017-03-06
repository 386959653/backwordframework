package com.pds.p2p.core.data.validation;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.core.io.ClassPathResource;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class FormValidator {
    public static void main(String[] args) {
        FormValidator validator = new FormValidator("test");
        try {
            String txt = FileUtils.readFileToString(new File("e:/t.txt"), CharEncoding.UTF_8);
            System.out.println(txt);
            validator.load(txt);
            System.out.println(validator.valid(ImmutableMap.of("username", "", "address", 12)));
            System.out.println(validator.toJs());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String REQUIRED = "required";
    public static String EMAIL = "email";
    public static String URL = "url";
    public static String DATE = "date";
    public static String NUMBER = "number";
    public static String DIGITS = "digits";
    public static String EQUALTO = "equalTo";
    public static String MAXLENGTH = "maxlength";
    public static String MINLENGTH = "minlength";
    public static String MAX = "max";
    public static String MIN = "min";

    private String form;
    private ListMultimap<String, Rule> rules = ArrayListMultimap.create();
    private Map<String, String> titles = Maps.newHashMap();
    private static JSONObject MESSAGES;

    public static synchronized void loadMessage(Locale locale) {
        if (MESSAGES != null) {
            MESSAGES.clear();
        }
        String path = "Messages";
        if (locale == Locale.CHINA || locale == Locale.CHINESE) {
            path += locale.toString();
        } else {
        }
        ClassPathResource pathResource = new ClassPathResource(path, FormValidator.class);
        String text;
        try {
            text = FileUtils.readFileToString(pathResource.getFile(), CharEncoding.UTF_8);
            MESSAGES = JSONObject.parseObject(text);
        } catch (IOException e) {
        }
    }

    static {
        loadMessage(Locale.ENGLISH);
    }

    public FormValidator(String form) {
        this.form = form;
    }

    public FormValidator addRule(String name, Rule rule) {
        rules.put(name, rule);
        return this;
    }

    public FormValidator addMessage(String name, String method, String message) {
        List<Rule> list = rules.get(name);
        for (Rule rule : list) {
            if (StringUtils.endsWithIgnoreCase(method, rule.getMethod())) {
                rule.setMessage(message);
            }
        }
        return this;
    }

    private String js;

    public void load(String options) {
        rules.clear();
        titles.clear();
        JSONObject jsonOptions = JSONObject.parseObject(options);
        loadRules(jsonOptions);
        loadMessages(jsonOptions);
        loadTitles(jsonOptions);
        StringBuilder sb = new StringBuilder();
        jsonOptions.remove("titles");
        sb.append("$('#").append(this.form).append("').validate(");
        sb.append(jsonOptions.toJSONString());
        sb.append(")");
        this.js = sb.toString();
    }

    @SuppressWarnings("unchecked")
    public Object normal(Object soruces) {
        if (TypeUtils.isInstance(soruces, ImmutableMap.class)) {
            soruces = Maps.newHashMap((Map<String, Object>) soruces);
        } else if (TypeUtils.isInstance(soruces, Map.class)) {
            soruces = (Map<String, Object>) soruces;
        }
        if (TypeUtils.isInstance(soruces, Map.class)) {
            Map<String, Object> map = (Map<String, Object>) soruces;
            Set<String> set = this.titles.keySet();
            for (String key : set) {
                if (!map.containsKey(key)) {
                    map.put(key, null);
                }
            }
        }
        return soruces;
    }

    public String toJs() {
        return js;
    }

    public List<String> valid(Object soruce) {
        List<String> result = Lists.newArrayList();
        MetaObject metaSoruce = SystemMetaObject.forObject(normal(soruce));
        String getters[] = metaSoruce.getGetterNames();
        for (String getter : getters) {
            Object val = metaSoruce.getValue(getter);
            List<Rule> ruleItems = this.rules.get(getter);
            if (ruleItems == null) {
                continue;
            }
            for (Rule rule : ruleItems) {
                try {
                    Object[] args = new Object[] {rule.getParameters(), val, metaSoruce};
                    Class<?>[] parameterTypes = new Class[] {Object.class, Object.class, MetaObject.class};
                    Object object = MethodUtils.invokeStaticMethod(Validaty.class, rule.getMethod(),//
                            args, parameterTypes);
                    if ((Boolean) object == false) {
                        String msg = rule.getMessage();
                        if (msg == null) {
                            msg = MESSAGES.getString(rule.getMethod());
                        }
                        result.add(this.titles.get(getter) + ":" + MessageFormatter.formate(msg, rule.getParameters()));
                    }
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    private void loadMessages(JSONObject jsonOptions) {
        JSONObject messages = jsonOptions.getJSONObject("messages");
        if (messages == null) {
            return;
        }
        for (Map.Entry<String, Object> ent : messages.entrySet()) {
            String name = ent.getKey();
            Object val = ent.getValue();
            if (TypeUtils.isInstance(val, String.class)) {
                addMessage(name, "required", (String) val);
            } else {
                JSONObject jsonVal = (JSONObject) val;
                for (Map.Entry<String, Object> jsonent : jsonVal.entrySet()) {
                    addMessage(name, jsonent.getKey(), (String) jsonent.getValue());
                }
            }
        }
    }

    private void loadTitles(JSONObject jsonOptions) {
        JSONObject jsontitles = jsonOptions.getJSONObject("titles");
        if (jsontitles == null) {
            return;
        }
        for (Map.Entry<String, Object> ent : jsontitles.entrySet()) {
            String name = ent.getKey();
            String val = (String) ent.getValue();
            this.titles.put(name, val);
        }
    }

    private void loadRules(JSONObject jsonOptions) {
        JSONObject rules = jsonOptions.getJSONObject("rules");
        for (Map.Entry<String, Object> ent : rules.entrySet()) {
            String name = ent.getKey();
            Object val = ent.getValue();
            if (TypeUtils.isInstance(val, String.class)) {
                addRule(name, new Rule((String) val, Boolean.TRUE));
            } else {
                JSONObject jsonVal = (JSONObject) val;
                for (Map.Entry<String, Object> jsonent : jsonVal.entrySet()) {
                    addRule(name, new Rule((String) jsonent.getKey(), jsonent.getValue()));
                }
            }
        }
    }

}
