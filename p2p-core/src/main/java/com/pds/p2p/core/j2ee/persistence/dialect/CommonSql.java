/**
 *
 */
package com.pds.p2p.core.j2ee.persistence.dialect;

import java.util.Map;

import com.pds.p2p.core.j2ee.persistence.entity.EntityInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.apache.ibatis.logging.Log;
import org.apache.ibatis.logging.LogFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;

/**
 * 生成通用的SQL，适用于所有数据库
 *
 * @author 王文
 * @date 2015-8-13 下午12:30:21
 */
public class CommonSql {
    static protected Log logger = LogFactory.getLog(CommonSql.class);

    static public <T> String insertNameSql(EntityInfo<T> entityInfo) {
        SQL sql = new SQL();
        Map<String, String> fieldMappings = entityInfo.fieldMappings();
        sql.INSERT_INTO(entityInfo.tableName());
        for (Map.Entry<String, String> ent : fieldMappings.entrySet()) {
            sql.VALUES(ent.getValue(), ":" + ent.getKey());
        }
        String retSql = sql.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(retSql);
        }
        return retSql;
    }

    static public <T> String updateNameSql(EntityInfo<T> entityInfo, T entity) {
        SQL sql = new SQL();
        Map<String, String> fieldMappings = entityInfo.fieldMappings();
        sql.UPDATE(entityInfo.tableName());
        MetaObject metaObject = SystemMetaObject.forObject(entity);
        String idNm = entityInfo.getIdFieldName();
        for (Map.Entry<String, String> ent : fieldMappings.entrySet()) {
            String fieldNm = ent.getKey();
            String dbField = ent.getValue();
            if (StringUtils.equalsIgnoreCase(idNm, fieldNm)) {
                continue;
            }
            Object val = metaObject.getValue(fieldNm);
            if (val != null) {
                sql.SET(String.format("%s=:%s", dbField, fieldNm));
            }
        }
        sql.WHERE(String.format("%s=:%s", fieldMappings.get(entityInfo.getIdFieldName()), entityInfo.getIdFieldName()));
        String retSql = sql.toString();
        if (logger.isDebugEnabled()) {
            logger.debug(retSql);
        }
        return retSql;
    }

}
