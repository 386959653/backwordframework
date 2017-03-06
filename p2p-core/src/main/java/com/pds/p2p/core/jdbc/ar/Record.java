package com.pds.p2p.core.jdbc.ar;

import java.util.Map;
import java.util.Set;

import com.pds.p2p.core.jdbc.helper.EnMap;
import com.pds.p2p.core.jdbc.meta.ColumnMeta;
import com.pds.p2p.core.utils.UtilType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 行记录对象。
 * <p>
 * 行记录的key值，不缺分大小写以及下划线，所以要求创建表时，不允许相同的字母（下滑线不一个位置）
 *
 * @author redraiment
 * @since 1.0
 */
public final class Record {
    private final Table table;
    private final EnMap<String, Object> values;

    public Record(Table table, Map<String, Object> values) {
        this.table = table;
        this.values = new EnMap<String, Object>(values);
    }

    public Record(Table table, String[] fields, Object[] values) {
        this.table = table;
        this.values = new EnMap<String, Object>();
        int idx = 0;
        for (; idx < fields.length; idx++) {
            if (values[idx] != null) {
                this.values.put(fields[idx], values[idx]);
            }
        }
    }

    public Record(Table table) {
        this.table = table;
        this.values = new EnMap<String, Object>();
    }

    public Set<String> columnNames() {
        return values.keySet();
    }

    @SuppressWarnings("unchecked")
    public <E> E get(String name) {
        name = DB.parseKeyParameter(name);
        Object value = null;
        if (values.containsKey(name)) {
            value = values.get(name);
        } else if (table.relations.containsKey(name)) {
            Association relation = table.relations.get(name);
            Table active = table.dbo.active(relation.target);
            if (relation.isAncestor() && !relation.isCross()) {
                active.constrain(relation.targetKey, get(relation.sourceKey));
            } else {
                String joinStr = relation.assoc(table.tableName, (Number) get("id"));
                active.join(joinStr);
            }
            value = (relation.isOnlyOneResult() ? active.first() : active);
        }
        String key = "get_".concat(name);
        if (table.hooks.containsKey(key)) {
            value = table.hooks.get(key).call(this, value);
        }
        return (E) value;
    }

    /* For primitive types */
    public boolean getBool(String name) {
        return get(name);
    }

    public byte getByte(String name) {
        return get(name);
    }

    public char getChar(String name) {
        return get(name);
    }

    public short getShort(String name) {
        return get(name);
    }

    public int getInt(String name) {
        return get(name);
    }

    public long getLong(String name) {
        return get(name);
    }

    public float getFloat(String name) {
        return get(name);
    }

    public double getDouble(String name) {
        return get(name);
    }

	/* For any other types */

    public String getStr(String name) {
        return get(name);
    }

    public <E> E get(String name, Class<E> type) {
        return type.cast(get(name));
    }

    public Record set(String name, Object value) {
        name = DB.parseKeyParameter(name);
        String key = "set_".concat(name);
        if (table.hooks.containsKey(key)) {
            value = table.hooks.get(key).call(this, value);
        }
        values.put(name, value);
        return this;
    }

    public Record copyFrom(Object soruce) {
        this.values.clear();
        MetaObject metaSoruce = SystemMetaObject.forObject(soruce);
        String getters[] = metaSoruce.getGetterNames();
        for (String getter : getters) {
            if (this.table.getTableMeta().containsColumn(getter)) {
                Object val = metaSoruce.getValue(getter);
                this.values.put(getter, val);
            }
        }
        return this;
    }

    @SuppressWarnings("unchecked")
    public Record copyTo(Object target) {
        if (target instanceof Map) {
            ((Map<String, Object>) target).clear();
            ((Map<String, Object>) target).putAll(this.values);
        } else {
            UtilType.initializeNull(target);
            MetaObject metaTarget = SystemMetaObject.forObject(target);
            String setters[] = metaTarget.getSetterNames();
            for (String setter : setters) {
                if (this.table.getTableMeta().containsColumn(setter)) {
                    metaTarget.setValue(setter, this.values.get(setter));
                }
            }
        }
        return this;
    }

    public Record save() {
        ColumnMeta pkCol = table.getPkColumnMeta();
        String pkName = pkCol.getName();
        Object idVal = this.values.get(pkName);
        if (idVal == null) {
            Record r = table.create(this);
            this.values.put(pkName, r.get(pkName));
        } else {
            table.updateByPrimaryKey(this);
        }
        return this;
    }

    public Record update(Object... args) {
        for (int i = 0; i < args.length; i += 2) {
            set(args[i].toString(), args[i + 1]);
        }
        return save();
    }

    public void destroy() {
        table.deleteByPrimaryKey(this);
    }

    @Override
    public String toString() {
        StringBuilder line = new StringBuilder();
        for (Map.Entry<String, Object> e : values.entrySet()) {
            line.append(String.format("%s = %s\n", e.getKey(), e.getValue()));
        }
        return line.toString();
    }
}
