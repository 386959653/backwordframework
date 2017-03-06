/**
 *
 */
package com.pds.p2p.core.j2ee.persistence.entity;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.jdbc.core.SqlTypeValue;
import org.springframework.jdbc.core.StatementCreatorUtils;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import com.pds.p2p.core.jdbc.meta.ColumnMeta;
import com.pds.p2p.core.jdbc.meta.DefaultSizes;
import com.pds.p2p.core.utils.StringUtils;
import com.pds.p2p.core.utils.UtilType;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

/**
 * @author 王文
 * @date 2015-8-5 下午6:43:47
 */
public class EntityInfo<T> {
    private final Class<T> domainClass;
    private final Field idField;
    private final Map<String, String> fieldMappings = Maps.newLinkedHashMap();

    /**
     * Creates a new {@link AbstractEntityInformation} from the given domain
     * class.
     *
     * @param domainClass must not be {@literal null}.
     */
    public EntityInfo(Class<T> domainClass) {
        Assert.notNull(domainClass);
        this.domainClass = domainClass;
        Field field = null;
        // 确定id字段
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(domainClass, Id.class);
        if (!CollectionUtils.isEmpty(fields)) {
            Validate.isTrue(fields.size() == 1);
            field = fields.get(0);
        } else {
            field = FieldUtils.getDeclaredField(domainClass, "id", true);
        }
        Assert.notNull(field, "must has field of id");
        this.idField = field;
        this.idField.setAccessible(true);
    }

    public boolean isNew(T entity) {
        Object id = getId(entity);
        Class<?> idType = getIdType();
        if (!idType.isPrimitive()) {
            return id == null;
        }
        if (id instanceof Number) {
            return ((Number) id).longValue() == 0L;
        }
        throw new IllegalArgumentException(String.format("Unsupported primitive id type %s!", idType));
    }

    public Class<T> getJavaType() {
        return this.domainClass;
    }

    public Object getId(T entity) {
        try {
            Validate.notNull(idField, "no idField");
            return idField.get(entity);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
            throw Throwables.propagate(e);
        }
    }

    public void setId(T entity, Object val) {
        try {
            val = UtilType.convert(val, idField.getType());
            idField.set(entity, val);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            throw Throwables.propagate(e);
        }
    }

    public Class<?> getIdType() {
        return this.idField.getType();
    }

    public boolean isIdField(String idname) {
        return StringUtils.equalsIgnoreCase(idField.getName(), idname);
    }

    public String getIdFieldName() {
        return idField.getName();
    }

    public GenerationType idGenerationType() {
        Assert.notNull(idField);
        GeneratedValue generatedValue = idField.getAnnotation(GeneratedValue.class);
        if (generatedValue == null) {
            return null;
        }
        return generatedValue.strategy();
    }

    public String tableName() {
        Table table = domainClass.getAnnotation(Table.class);
        if (table != null && StringUtils.isNotEmpty(table.name())) {
            return table.name();
        }
        String tableNm = domainClass.getSimpleName();
        return StringUtils.toUnderScoreCase(tableNm);
    }

    public Map<String, String> fieldMappings() {
        if (fieldMappings.isEmpty()) {
            List<Field> fields = FieldUtils.getAllFieldsList(domainClass);
            for (Field field : fields) {
                if (isTransient(field)) {
                    continue;
                }
                String beanFldNm = field.getName();
                String dbFldNm = StringUtils.EMPTY;
                Column column = field.getAnnotation(Column.class);
                if (column != null && StringUtils.isNotEmpty(column.name())) {
                    dbFldNm = column.name();
                } else {
                    dbFldNm = StringUtils.toUnderScoreCase(beanFldNm);
                }
                fieldMappings.put(beanFldNm, dbFldNm);
            }
        }
        return fieldMappings;
    }

    public List<ColumnMeta> buildColumnMetas() {
        List<ColumnMeta> result = Lists.newArrayList();
        List<Field> fields = FieldUtils.getAllFieldsList(domainClass);
        for (Field field : fields) {
            if (isTransient(field)) {
                continue;
            }
            int typeCode = StatementCreatorUtils.javaTypeToSqlParameterType(field.getType());

            if (SqlTypeValue.TYPE_UNKNOWN == typeCode) {
                continue;
            }

            String beanFldNm = field.getName();
            String dbFldNm = StringUtils.EMPTY;

            Column column = field.getAnnotation(Column.class);

            if (column != null && StringUtils.isNotEmpty(column.name())) {
                dbFldNm = column.name();
            } else {
                dbFldNm = StringUtils.toUnderScoreCase(beanFldNm);
            }

            ColumnMeta columnInfo = new ColumnMeta();

            columnInfo.setJavaName(beanFldNm);
            columnInfo.setName(dbFldNm);

            columnInfo.setTypeCode(typeCode);

            if (column != null) {
                if (columnInfo.isOfTextType()) {
                    columnInfo.setSize(String.valueOf(column.length()));
                } else if (columnInfo.isOfNumericType()) {
                    columnInfo.setPrecisionRadix(column.precision());
                    columnInfo.setScale(column.scale());
                }
                columnInfo.setRequired(!column.nullable());
            } else {
                columnInfo.setSize(DefaultSizes.get(typeCode));
            }

            Id id = field.getAnnotation(Id.class);
            if (id != null) {
                columnInfo.setPrimaryKey(true);
                columnInfo.setRequired(true);
            }

            ColumnComment columnComment = field.getAnnotation(ColumnComment.class);
            if (columnComment != null) {
                columnInfo.setDescription(columnComment.value());
            }

            // System.out.println(columnInfo.toVerboseString());

            result.add(columnInfo);
        }
        return result;
    }

    /**
     * 不持久化的字段
     *
     * @param field
     *
     * @return
     */
    public boolean isTransient(Field field) {
        int modifiers = field.getModifiers();
        boolean isTransient = (field.getAnnotation(Transient.class) != null);
        if (isTransient || Modifier.isTransient(modifiers) || Modifier.isStatic(modifiers)) {
            return true;
        }
        return false;
    }
}
