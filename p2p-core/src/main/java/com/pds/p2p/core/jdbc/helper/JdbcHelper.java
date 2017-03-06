package com.pds.p2p.core.jdbc.helper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pds.p2p.core.jdbc.mapper.CamelCaseNameColumnMapRowMapper;
import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.reflect.TypeUtils;
import org.springframework.dao.support.DataAccessUtils;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import com.google.common.base.Throwables;

public class JdbcHelper implements JdbcHelperIF {
    public static void main(String[] args) {
        List<Range<Integer>> ranges = Pagination.ranges(12, 3);
        System.out.println(ranges);

    }

    final private JdbcTemplate jdbcTemplate;
    final private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public JdbcHelper(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    /*
     *
     */
    @Override
    public <T> T directQueryForObject(Class<T> cls, String sql, Object... args) {
        List<T> results = directQueryForList(cls, sql, args);
        return DataAccessUtils.singleResult(results);
    }

    @Override
    public Map<String, Object> directQueryForMap(String sql, Object... args) {
        List<Map<String, Object>> results = directQueryForMapList(sql, args);
        return DataAccessUtils.singleResult(results);
    }

    @Override
    public Map<String, Object> namedQueryForMap(String sql, Map<String, ?> paramMap) {
        List<Map<String, Object>> results = namedQueryForMapList(sql, paramMap);
        return DataAccessUtils.singleResult(results);
    }

    /*
     *
     * @see com.pds.j2ee.jdbc.JdbcHelperIF#namedQueryForObject(java.lang.Class,
     * java.lang.String, java.util.Map)
     */
    @Override
    public <T> T namedQueryForObject(Class<T> cls, String sql, Map<String, ?> paramMap) {
        List<T> results = namedQueryForList(cls, sql, paramMap);
        return DataAccessUtils.singleResult(results);
    }

    @Override
    public <T> List<T> directQueryForList(Class<T> cls, String sql, Object... args) {
        if (TypeUtils.isAssignable(cls, String.class) || TypeUtils.isAssignable(cls, Number.class) || TypeUtils
                .isAssignable(cls, Date.class)) {
            return this.directQueryForListSingleColumn(cls, sql, args);
        } else {
            return jdbcTemplate.query(sql, args, new BeanPropertyRowMapper<T>(cls));
        }
    }

    @Override
    public List<Map<String, Object>> directQueryForMapList(String sql, Object... args) {
        return jdbcTemplate.query(sql, args, new CamelCaseNameColumnMapRowMapper());
    }

    @Override
    public List<Map<String, Object>> namedQueryForMapList(String sql, Map<String, ?> paramMap) {
        return namedParameterJdbcTemplate.query(sql, paramMap, new CamelCaseNameColumnMapRowMapper());
    }

    @Override
    public <T> List<T> namedQueryForList(Class<T> cls, String sql, Map<String, ?> paramMap) {
        if (TypeUtils.isAssignable(cls, String.class) || TypeUtils.isAssignable(cls, Number.class) || TypeUtils
                .isAssignable(cls, Date.class)) {
            return this.namedQueryForListSingleColumn(cls, sql, paramMap);
        } else {
            return namedParameterJdbcTemplate.query(sql, paramMap, new BeanPropertyRowMapper<T>(cls));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.pds.j2ee.jdbc.JdbcHelperIF#directQueryForListSingleColumn(java.lang
     * .Class, java.lang.String, java.lang.Object)
     */
    @Override
    public <T> List<T> directQueryForListSingleColumn(Class<T> requiredType, String sql, Object... args) {
        List<T> results = jdbcTemplate.queryForList(sql, requiredType, args);
        return results;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.pds.j2ee.jdbc.JdbcHelperIF#namedQueryForListSingleColumn(java.lang
     * .Class, java.lang.String, java.util.Map)
     */
    @Override
    public <T> List<T> namedQueryForListSingleColumn(Class<T> requiredType, String sql, Map<String, ?> paramMap) {
        List<T> results = namedParameterJdbcTemplate.queryForList(sql, paramMap, requiredType);
        return results;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pds.j2ee.jdbc.JdbcHelperIF#directExecuteUpdate(java.lang.String,
     * java.lang.Object)
     */
    @Override
    public int directExecuteUpdate(String sql, Object... args) {
        int result = jdbcTemplate.update(sql, args);
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.pds.j2ee.jdbc.JdbcHelperIF#namedExecuteUpdate(java.lang.String,
     * java.util.Map)
     */
    @Override
    public int namedExecuteUpdate(String sql, Map<String, ?> paramMap) {
        int result = namedParameterJdbcTemplate.update(sql, paramMap);
        return result;
    }

    @Override
    public void directBatchUpdate(String sql, List<Object[]> batchArgs, int commitSize) {
        List<Range<Integer>> ranges = Pagination.ranges(batchArgs.size(), commitSize);
        for (Range<Integer> range : ranges) {
            List<Object[]> subBatchArgs = batchArgs.subList(range.getMinimum(), range.getMaximum());
            this.directBatchUpdate(sql, subBatchArgs);
        }
    }

    @Override
    public void directBatchUpdate(final String sql, final List<Object[]> batchArgs) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        DataSourceTransactionManager transactionManager =
                new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, def);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    jdbcTemplate.batchUpdate(sql, batchArgs);
                } catch (Exception e) {
                    status.setRollbackOnly(); // 回滚
                    throw Throwables.propagate(e);
                }
            }
        });
    }

    @Override
    public void namedBatchUpdate(String sql, final List<Map<String, Object>> dat, int commitSize) {
        List<Range<Integer>> ranges = Pagination.ranges(dat.size(), commitSize);
        for (Range<Integer> range : ranges) {
            List<Map<String, Object>> subBatchArgs = dat.subList(range.getMinimum(), range.getMaximum());
            this.namedBatchUpdate(sql, subBatchArgs);
        }
    }

    @Override
    public void namedBatchUpdate(final String sql, final List<Map<String, Object>> dat) {
        final SqlParameterSource[] batchArgs = new SqlParameterSource[dat.size()];
        int i = 0;
        for (Map<String, Object> values : dat) {
            batchArgs[i] = new MapSqlParameterSource(values);
            i++;
        }
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        DataSourceTransactionManager transactionManager =
                new DataSourceTransactionManager(jdbcTemplate.getDataSource());
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager, def);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                try {
                    namedParameterJdbcTemplate.batchUpdate(sql, batchArgs);
                } catch (Exception e) {
                    status.setRollbackOnly(); // 回滚
                    throw Throwables.propagate(e);
                }
            }
        });
    }
}
