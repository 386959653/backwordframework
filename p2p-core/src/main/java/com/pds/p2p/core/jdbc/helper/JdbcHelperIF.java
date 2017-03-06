package com.pds.p2p.core.jdbc.helper;

import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;

public interface JdbcHelperIF {
    /**
     * Query given SQL to create a prepared statement from SQL and a list of
     * arguments to bind to the query, mapping each row to a Java object
     *
     * @param cls  object that will map one object of cls
     * @param sql  SQL query to execute
     * @param args arguments to bind to the query (leaving it to the
     *             PreparedStatement to guess the corresponding SQL type);
     *
     * @return the result List, containing mapped objects
     *
     * @throws DataAccessException if the query fails
     */
    public <T> T directQueryForObject(Class<T> cls, String sql, Object... args);

    public <T> T namedQueryForObject(Class<T> cls, String sql, Map<String, ?> paramMap);

    public Map<String, Object> namedQueryForMap(String sql, Map<String, ?> paramMap);

    public Map<String, Object> directQueryForMap(String sql, Object... args);

    public <T> List<T> directQueryForList(Class<T> cls, String sql, Object... args);

    public <T> List<T> namedQueryForList(Class<T> cls, String sql, Map<String, ?> paramMap);

    public List<Map<String, Object>> directQueryForMapList(String sql, Object... args);

    public List<Map<String, Object>> namedQueryForMapList(String sql, Map<String, ?> paramMap);

    public <T> List<T> directQueryForListSingleColumn(Class<T> requiredType, String sql, Object... args);

    public <T> List<T> namedQueryForListSingleColumn(Class<T> requiredType, String sql, Map<String, ?> paramMap);

    public int directExecuteUpdate(String sql, Object... args);

    public int namedExecuteUpdate(String sql, Map<String, ?> paramMap);

    public void directBatchUpdate(final String sql, final List<Object[]> batchArgs, final int commitSize);

    public void directBatchUpdate(final String sql, final List<Object[]> batchArgs);

    public void namedBatchUpdate(String sql, final List<Map<String, Object>> dat, int commitSize);

    public void namedBatchUpdate(final String sql, final List<Map<String, Object>> dat);

}