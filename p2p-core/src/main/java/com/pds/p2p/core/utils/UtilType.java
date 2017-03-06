package com.pds.p2p.core.utils;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.StringWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.util.Assert;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.NameFilter;
import com.alibaba.fastjson.serializer.PropertyFilter;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;

/**
 * 提供类型转换工具
 *
 * @author wangwen
 * @version 2015-07-15
 */
public class UtilType {
    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_ZH = "yyyy年MM月dd日";
    public static final String YYYY_MM_DD_T_HH_MM_SS = "yyyy-MM-dd'T'HH:mm:ss";
    public static final String YYYY_MM_DD_T_00_00_00 = "yyyy-MM-dd'T'00:00:00";
    public static final String YYYY_MM_DD_EN = "yyyy/MM/dd";
    public static final String YYYY_M_D = "yyyy-M-d";

    public static final String MM_DD = "MM月dd日";
    public static final String MM_DD_HH_MM = "MM月dd日  HH:mm";
    public static final String MM_DD_HH_MM_SS = "MM月dd日  HH:mm:ss";

    public static final String HH_MM = "HH:mm";

    public static final String[] DT_FMTS = {YYYY_MM_DD_HH_MM_SS, YYYY_MM_DD, YYYY_M_D, //
            YYYY_MM_DD_ZH, YYYY_MM_DD_EN, YYYY_MM_DD_T_HH_MM_SS, YYYY_MM_DD_T_00_00_00, //
            "yyyy-MM-dd HH:mm", "yyyy-MM-dd HH:mm:ss.SSS"

    };

    public static void main(String[] args) {
        System.out.println(JSON.toJSON(ImmutableMap.of("a", "b")).getClass());
        List<String> a = Lists.newArrayList("a", "b", "c");
        System.out.println(com.alibaba.fastjson.util.TypeUtils.isGenericParamType(a.getClass()));
        //Field field = com.alibaba.fastjson.util.TypeUtils.getField(Td.class, "aa");
        //System.out.println(field.getGenericType());
        // public static Type getFieldType(Class<?> clazz, Type type, Type
        // fieldType) {
    }

    static class Td {
        List<String> aa;

        public List<String> getAa() {
            return aa;
        }

        public void setAa(List<String> aa) {
            this.aa = aa;
        }

    }

    public static Class<?> getRawClass(Type type) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getRawClass(((ParameterizedType) type).getRawType());
        } else {
            throw new JSONException("TODO");
        }
    }

    public static void populate(Object obj, XNode node, String... attr) {
        MetaObject metaObject = SystemMetaObject.forObject(obj);
        for (String nm : attr) {
            String val = node.getStringAttribute(nm);
            if (StringUtils.isNotEmpty(val)) {
                Class<?> requiredType = metaObject.getGetterType(nm);
                Object requiredVal = UtilType.convert(val, requiredType);
                metaObject.setValue(nm, requiredVal);
            }
        }
    }

    public static boolean isEmpty(Object inValue) {
        if (inValue == null) {
            return true;
        } else if (isJavaStringType(inValue.getClass())) {
            return StringUtils.isBlank((String) inValue);
        } else {
            return false;
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static Object convert(Object inValue, Class<?> requiredType) {
        if (inValue == null) {
            if (TypeUtils.isAssignable(requiredType, String.class)) {
                return StringUtils.EMPTY;
            }
            return null;
        }
        if (TypeUtils.isAssignable(inValue.getClass(), requiredType)) {
            return inValue;
        }
        if (requiredType.isEnum()) {
            Object obj = Enum.valueOf((Class) requiredType, inValue.toString());
            if (obj == null) {
                obj = Enum.valueOf((Class) requiredType, inValue.toString().toUpperCase());
            }
            return obj;
        }
        if (isJavaStringType(requiredType)) {
            if (TypeUtils.isAssignable(inValue.getClass(), Date.class)) {
                return DateFormatUtils.ISO_DATE_FORMAT.format((Date) inValue);
            } else {
                return com.alibaba.fastjson.util.TypeUtils.castToString(inValue);
            }
        } else if (boolean.class.equals(requiredType) || Boolean.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToBoolean(inValue);
        } else if (byte.class.equals(requiredType) || Byte.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToByte(inValue);
        } else if (short.class.equals(requiredType) || Short.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToShort(inValue);
        } else if (int.class.equals(requiredType) || Integer.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToInt(inValue);
        } else if (long.class.equals(requiredType) || Long.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToLong(inValue);
        } else if (float.class.equals(requiredType) || Float.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToFloat(inValue);
        } else if (double.class.equals(requiredType) || Double.class.equals(requiredType)) {
            return com.alibaba.fastjson.util.TypeUtils.castToDouble(inValue);
        } else if (BigDecimal.class.equals(requiredType)) {
            try {
                return com.alibaba.fastjson.util.TypeUtils.castToBigDecimal(inValue);
            } catch (Exception e) {
                throw new IllegalArgumentException("can not cast to BigDecimal, value : " + inValue);
            }
        } else if (isJavaDateType(requiredType)) {
            Date date = null;
            if (TypeUtils.isAssignable(inValue.getClass(), Date.class)) {
                date = (Date) inValue;
            } else if (TypeUtils.isAssignable(inValue.getClass(), Number.class)) {
                date = new Date((Long) inValue);
            } else {
                String val = inValue.toString();
                try {
                    date = DateUtils.parseDate(val, DT_FMTS);
                } catch (ParseException e) {
                    try {
                        date = DateUtils.parseDate(val, "yyyyDDmm", "yyyyMMddHHmmss", "HH:mm:ss");
                    } catch (ParseException e1) {
                        throw new IllegalArgumentException("can not cast to Date, value : " + inValue);
                    }
                }
            }
            return date;
        } else {
            Object retObj;
            try {
                retObj = ConstructorUtils.invokeConstructor(requiredType);
                copyProperties(retObj, inValue);
                return retObj;
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException |
                    InstantiationException e) {
                throw new IllegalArgumentException(
                        String.format("不能转换相应的FROM [%s] TO [%s].", inValue.getClass().getName(),
                                requiredType.getName()));
            }

        }
    }

    /***
     * 将列表里的对象的某字段，转换为日期型
     *
     * @param list
     * @param reqDateFields
     */
    public static void convertToDate(List<?> list, String... reqDateFields) {
        for (Object o : list) {
            fieldToDate(o, reqDateFields);
        }
    }

    /***
     * 将map内的值，如果是date形式，统一转换成字符串格式
     *
     * @param map
     * @param fmtPatten
     */
    public static void fmtAllDate(Map<String, Object> map, String fmtPatten) {
        List<String> flds = Lists.newArrayList();
        for (Map.Entry<String, Object> ent : map.entrySet()) {
            Object val = ent.getValue();
            if (val != null && isJavaDateType(val.getClass())) {
                flds.add(ent.getKey());
            }
        }
        for (String fld : flds) {
            Object val = map.get(fld);
            map.put(fld, DateFormatUtils.format((Date) val, fmtPatten));
        }
    }

    public static void fmtAllDate(List<Map<String, Object>> list, String fmtPatten) {
        for (Map<String, Object> item : list) {
            fmtAllDate(item, fmtPatten);
        }
    }

    public static void fieldToDate(Object o, String... reqDateFields) {
        MetaObject metaObject = SystemMetaObject.forObject(o);
        for (String req : reqDateFields) {
            if (metaObject.hasSetter(req) && metaObject.hasGetter(req)) {
                Object val = metaObject.getValue(req);
                if (val != null) {
                    metaObject.setValue(req, convert(metaObject.getValue(req), Date.class));
                }
            }
        }
    }

    public static boolean isJavaStringType(Class<?> javaType) {
        return (CharSequence.class.isAssignableFrom(javaType) || StringWriter.class.isAssignableFrom(javaType));
    }

    public static boolean isJavaDateType(Class<?> javaType) {
        return java.util.Date.class.isAssignableFrom(javaType);
    }

    public static void setValue(MetaObject metaObject, String propName, Object inValue) {
        if (inValue == null) {
            return;
        }
        if (metaObject.hasSetter(propName)) {
            Class<?> requiredType = metaObject.getSetterType(propName);
            try {
                Object val = UtilType.convert(inValue, requiredType);
                metaObject.setValue(propName, val);
            } catch (Exception e) {
                System.out.println(String.format("%s:%s", propName, e.getMessage()));
                metaObject.setValue(propName, null);
            }
        }
    }

    public static void initializeNull(Object target) {
        MetaObject metaTarget = SystemMetaObject.forObject(target);
        String setters[] = metaTarget.getSetterNames();
        for (String setter : setters) {
            try {
                metaTarget.setValue(setter, null);
            } catch (Exception ex) {
            }
        }
    }

    public static void initializeFields(Object target) {
        MetaObject metaTarget = SystemMetaObject.forObject(target);
        String setters[] = metaTarget.getSetterNames();
        for (String setter : setters) {
            try {
                Object val = metaTarget.getValue(setter);
                if (val != null) {
                    continue;
                }
                Class<?> scls = metaTarget.getSetterType(setter);
                if (TypeUtils.isAssignable(scls, Number.class)) {
                    metaTarget.setValue(setter, 0);
                } else if (TypeUtils.isAssignable(scls, Boolean.class)) {
                    metaTarget.setValue(setter, false);
                } else if (UtilType.isJavaStringType(scls)) {
                    metaTarget.setValue(setter, StringUtils.EMPTY);
                } else if (UtilType.isJavaDateType(scls)) {
                    metaTarget.setValue(setter, new Date());
                } else if (TypeUtils.isArrayType(scls)) {

                } else if (TypeUtils.isAssignable(scls, List.class)) {

                } else if (TypeUtils.isAssignable(scls, Map.class)) {

                } else {
                    Object subObj = scls.newInstance();
                    initializeFields(subObj);
                }
            } catch (Exception ex) {

            }
        }
    }

    public static void copy(MetaObject target, Map<Object, Object> soruce) {
        if (soruce == null) {
            return;
        }
        for (Map.Entry<Object, Object> ent : soruce.entrySet()) {
            setValue(target, (String) ent.getKey(), ent.getValue());
        }
    }

    /***
     * 仅拷贝第一层的属性,可以实现map和pojo，pojo和pojo，map和map之间的复制
     *
     * @param target 目标对象
     * @param soruce 源对象
     *
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static Object copyProperties(Object target, Object soruce) {
        if (soruce != null) {
            MetaObject metaSoruce = SystemMetaObject.forObject(soruce);
            if (target instanceof Map) {
                String getters[] = metaSoruce.getGetterNames();
                Map<String, Object> map = (Map<String, Object>) target;
                for (String getter : getters) {
                    map.put(getter, metaSoruce.getValue(getter));
                }
            } else {
                MetaObject metaTarget = SystemMetaObject.forObject(target);
                String setters[] = metaTarget.getSetterNames();
                for (String setter : setters) {
                    try {
                        setValue(metaTarget, setter, metaSoruce.getValue(setter));
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return target;
    }

    /***
     * @param target
     * @param soruce
     * @param nullReplace 如果true，允许source的null值覆盖target对象相同的字段值；
     *
     * @return
     */
    @SuppressWarnings({"unchecked"})
    public static Object copyProperties(Object target, Object soruce, boolean nullReplace) {
        if (soruce != null) {
            MetaObject metaSoruce = SystemMetaObject.forObject(soruce);
            if (target instanceof Map) {
                String getters[] = metaSoruce.getGetterNames();
                Map<String, Object> map = (Map<String, Object>) target;
                for (String getter : getters) {
                    Object val = metaSoruce.getValue(getter);
                    if (!nullReplace && val == null) {
                        continue;
                    }
                    map.put(getter, val);
                }
            } else {
                MetaObject metaTarget = SystemMetaObject.forObject(target);
                String setters[] = metaTarget.getSetterNames();
                for (String setter : setters) {
                    try {
                        Object val = metaSoruce.getValue(setter);
                        if (!nullReplace && val == null) {
                            continue;
                        }
                        setValue(metaTarget, setter, metaSoruce.getValue(setter));
                    } catch (Exception ex) {

                    }
                }
            }
        }
        return target;
    }

    public static JSON convertJSON(Object o) {
        if (isJavaStringType(o.getClass())) {
            return (JSON) JSON.parse((String) o);
        }
        return (JSON) JSON.toJSON(o);
    }

    public static JSON strToJSON(String str) {
        try {
            return (JSON) JSON.parse(str);
        } catch (Exception ex) {
            return null;
        }
    }

    /***
     * 将map中含json格式的字符串也变成json對象
     *
     * @param map
     *
     * @return
     */
    public static JSONObject mapToJSON(Map<String, ?> map) {
        JSONObject ret = new JSONObject();
        for (Map.Entry<String, ?> ent : map.entrySet()) {
            String key = ent.getKey();
            Object val = ent.getValue();
            if (val == null) {
                continue;
            }
            if (TypeUtils.isArrayType(val.getClass())) {
                Object[] array = (Object[]) val;
                JSONArray list = new JSONArray(array.length);
                for (Object obj : array) {
                    if (isJavaStringType(obj.getClass())) {
                        JSON json = strToJSON(obj.toString());
                        if (json != null) {
                            list.add(json);
                        } else {
                            list.add(obj);
                        }
                    } else {
                        list.add(obj);
                    }
                }
                ret.put(key, list);
            } else {
                if (isJavaStringType(val.getClass())) {
                    JSON json = strToJSON(val.toString());
                    if (json != null) {
                        ret.put(key, json);
                    } else {
                        ret.put(key, val);
                    }
                } else {
                    ret.put(key, val);
                }
            }
        }
        return ret;
    }

    public static <T> List<T> list(List<T> list) {
        if (list != null) {
            return list;
        }
        return Collections.emptyList();
    }

    public static List<Map<String, Object>> toSafelist(List<Map<String, Object>> list) {
        List<Map<String, Object>> result = Lists.newArrayList();
        for (Map<String, Object> map : list) {
            result.add(new SafeMap(map));
        }
        return result;
    }

    public static <T> Map<String, T> map(Map<String, T> map) {
        if (map != null) {
            return map;
        }
        return Collections.emptyMap();
    }

    public static String toJSONString(Object o, final Set<String> preProperties, String dateFmt) {
        PropertyFilter prefilter = new PropertyFilter() {
            public boolean apply(Object source, String name, Object value) {
                return preProperties.contains(name);
            }
        };
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.getPropertyFilters().add(prefilter);
        if (dateFmt != null) {
            serializer.setDateFormat(dateFmt);
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
        }
        serializer.write(o);
        return out.toString();
    }

    public static String toJSONString(Object o, final Map<String, String> nameAndPropertyFilter) {
        return toJSONString(o, nameAndPropertyFilter, null);
    }

    public static String toJSONString(Object o, final Map<String, String> nameAndPropertyFilter, String dateFmt) {
        NameFilter nameFilter = new NameFilter() {
            public String process(Object source, String name, Object value) {
                return nameAndPropertyFilter.get(name);
            }
        };
        SerializeWriter out = new SerializeWriter();
        JSONSerializer serializer = new JSONSerializer(out);
        serializer.getNameFilters().add(nameFilter);
        if (dateFmt != null) {
            serializer.setDateFormat(dateFmt);
            serializer.config(SerializerFeature.WriteDateUseDateFormat, true);
        }
        serializer.write(o);
        return out.toString();
    }

    public static <T> Map<String, Object> toMap(T t) {
        JSONObject val = (JSONObject) JSONObject.toJSON(t);
        return val;
    }

    public static <T> List<Map<String, Object>> listToMapList(List<T> list) {
        List<Map<String, Object>> result = Lists.newArrayListWithCapacity(list.size());
        for (T t : list) {
            result.add(toMap(t));
        }
        return result;
    }

    public static Map<String, String> listToMap(List<?> list, String keyFld, String valFld) {
        Map<String, String> result = new LinkedHashMap<String, String>(list.size());
        for (Object obj : list) {
            MetaObject metaTarget = SystemMetaObject.forObject(obj);
            Object keyVal = metaTarget.getValue(keyFld);
            Object valVal = metaTarget.getValue(valFld);
            if (valFld != null && valVal != null) {
                result.put(keyVal.toString(), valVal.toString());
            }
        }
        return result;
    }

    public static <T> Map<String, T> listToMap(List<T> list, String keyFld) {
        if (CollectionUtils.isEmpty(list)) {
            return Collections.<String, T>emptyMap();
        }
        Map<String, T> result = new LinkedHashMap<String, T>(list.size());
        for (T obj : list) {
            MetaObject metaTarget = SystemMetaObject.forObject(obj);
            Object keyVal = metaTarget.getValue(keyFld);
            Validate.notNull(keyVal);
            result.put(keyVal.toString(), obj);
        }
        return result;
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> result = Lists.newArrayList();
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            result.add(field);
        }
        if (clazz.getSuperclass() != null) {
            result.addAll(getAllFields(clazz.getSuperclass()));
        }
        return Collections.unmodifiableList(result);
    }

    public static <T> List<T> defensiveCopy(@SuppressWarnings("unchecked") T... array) {
        return defensiveCopy(Arrays.asList(array));
    }

    public static <T> List<T> defensiveCopy(Iterable<T> iterable) {
        ArrayList<T> list = new ArrayList<T>();
        for (T element : iterable) {
            list.add(checkNotNull(element));
        }
        return list;
    }

    public static Method getMethodForName(Class<?> clazz, String methodName) {
        Assert.notNull(clazz, "Class must not be null");
        Assert.notNull(methodName, "Method name must not be null");
        Method[] declaredMethods = clazz.getMethods();
        for (Method method : declaredMethods) {
            if (methodName.equalsIgnoreCase(method.getName())) {
                return method;
            }
        }
        return null;
    }

    public static <T> T instanceOf(String cls)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException,
            InstantiationException {
        @SuppressWarnings("unchecked")
        Class<T> jobCls = (Class<T>) ClassUtils.getClass(cls);
        T obj = ConstructorUtils.invokeConstructor(jobCls);
        return obj;
    }

}
