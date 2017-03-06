/**
 *
 */
package com.pds.p2p.core.jdbc.dialect;

import org.apache.commons.lang3.NotImplementedException;

import com.pds.p2p.core.jdbc.dialect.db.MySQLDialect;
import com.pds.p2p.core.jdbc.dialect.db.OracleDialect;
import com.pds.p2p.core.jdbc.helper.DatabaseType;
import com.google.common.base.Throwables;

/**
 * @author 王文
 * @date 2015-8-5 下午3:09:06
 */
public class DialectFactory {
    private static Dialect MYSQL_DIALECT = new MySQLDialect();
    private static Dialect ORACLE_DIALECT = new OracleDialect();

    public static Dialect get(DatabaseType databaseType) {
        if (databaseType == DatabaseType.MYSQL) {
            return MYSQL_DIALECT;
        }
        if (databaseType == DatabaseType.ORACLE) {
            return ORACLE_DIALECT;
        }
        try {
            throw new NotImplementedException("暂时不支持" + databaseType.getProductName());
        } catch (NotImplementedException e) {
            throw Throwables.propagate(e);
        }
    }

}
