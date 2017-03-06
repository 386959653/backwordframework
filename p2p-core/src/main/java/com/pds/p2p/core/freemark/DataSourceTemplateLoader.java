package com.pds.p2p.core.freemark;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.SingleColumnRowMapper;

import com.pds.p2p.core.j2ee.service.JdbcHelperService;

import freemarker.cache.TemplateLoader;

/**
 * 用于freemarker从数据库装载template文件
 * <p>
 * <p>
 * 属性值配置实例,将生成如下的load sql:
 * <p>
 * <pre>
 * // select template_content from template where template_name=?
 * DataSourceTemplateLoader loader = new DataSourceTemplateLoader();
 * loader.setDataSource(ds);
 * loader.setTableName(&quot;template&quot;);
 * loader.setTemplateNameColumn(&quot;template_name&quot;);
 * loader.setTemplateContentColumn(&quot;template_content&quot;);
 * loader.setTimestampColumn(&quot;last_modified&quot;);
 * </pre>
 * <p>
 * mysql的表创建语句:
 * <p>
 * <pre>
 *  CREATE TABLE template (
 *   id bigint(20) PRIMARY KEY,
 *   template_name varchar(255) ,
 *   template_content text ,
 *   last_modified timestamp
 * )
 * </pre>
 *
 * @author badqiu
 */
public class DataSourceTemplateLoader extends JdbcHelperService implements TemplateLoader {
    private static Log log = LogFactory.getLog(DataSourceTemplateLoader.class);

    private String tableName;
    private String templateNameColumn;
    private String templateContentColumn;
    private String timestampColumn;

    @Override
    public Object findTemplateSource(String name) throws IOException {
        return null;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getTemplateNameColumn() {
        return templateNameColumn;
    }

    public void setTemplateNameColumn(String templateNameColumn) {
        this.templateNameColumn = templateNameColumn;
    }

    public String getTemplateContentColumn() {
        return templateContentColumn;
    }

    public void setTemplateContentColumn(String templateContentColumn) {
        this.templateContentColumn = templateContentColumn;
    }

    public String getTimestampColumn() {
        return timestampColumn;
    }

    public void setTimestampColumn(String timestampColumn) {
        this.timestampColumn = timestampColumn;
    }

    @Override
    public long getLastModified(Object templateSource) {
        if (StringUtils.isNotEmpty(timestampColumn)) {
            String templateName = (String) templateSource;
            Object timestamp = getJdbcTemplate().queryForObject(
                    getSql(timestampColumn), //
                    new Object[] {templateName}, //
                    new SingleColumnRowMapper<Object>());
            if (timestamp instanceof Number) {
                return ((Number) timestamp).longValue();
            } else if (timestamp instanceof Date) {
                return ((Date) timestamp).getTime();
            } else {
                throw new FreemarkerTemplateException(
                        "error template timestamp column type,must be Date or Number type");
            }
        }
        return -1;
    }

    @Override
    public Reader getReader(Object templateSource, final String encoding) throws IOException {
        final String templateName = (String) templateSource;
        if (log.isDebugEnabled()) {
            log.debug(templateName);
        }
        return getJdbcTemplate()
                .query(getSql(templateContentColumn), new Object[] {templateName}, new ResultSetExtractor<Reader>() {
                    public Reader extractData(ResultSet rs) throws SQLException, DataAccessException {
                        while (rs.next()) {
                            try {
                                Object obj = rs.getObject(templateContentColumn);
                                if (obj instanceof String) {
                                    return new StringReader((String) obj);
                                } else if (obj instanceof Clob) {
                                    return new StringReader(rs.getString(templateContentColumn));
                                } else if (obj instanceof InputStream) {
                                    return new InputStreamReader((InputStream) obj, encoding);
                                } else if (obj instanceof Blob) {
                                    return new InputStreamReader(rs.getBinaryStream(templateContentColumn), encoding);
                                } else {
                                    throw new FreemarkerTemplateException(
                                            "error sql type of templateContentColumn:" + templateContentColumn);
                                }
                            } catch (UnsupportedEncodingException e) {
                                throw new FreemarkerTemplateException(
                                        "load template from dataSource with templateName:" + templateName
                                                + " occer UnsupportedEncodingException", e);
                            }
                        }
                        throw new FreemarkerTemplateException(
                                "not found template from dataSource with templateName:" + templateName);
                    }
                });
    }

    @Override
    public void closeTemplateSource(Object templateSource) throws IOException {

    }

    private String getSql(String columnNames) {
        return "select " + columnNames + " from " + tableName + " where " + templateNameColumn + "=?";
    }

    private JdbcTemplate getJdbcTemplate() {
        return this.getJdbcHelper().getJdbcTemplate();
    }

}
