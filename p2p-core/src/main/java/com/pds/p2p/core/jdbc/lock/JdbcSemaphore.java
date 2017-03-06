package com.pds.p2p.core.jdbc.lock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;

import javax.sql.DataSource;

import org.apache.commons.collections4.Closure;
import org.apache.commons.lang3.Validate;
import org.quartz.impl.jdbcjobstore.LockException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class JdbcSemaphore implements InitializingBean {
    private final Logger log = LoggerFactory.getLogger(getClass());

    public static final String SELECT_FOR_LOCK = "SELECT * FROM %s  WHERE %s = ? FOR UPDATE";
    public static final String INSERT_LOCK = "INSERT INTO %s (%s) VALUES (?)";

	/*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 * 
	 * Data members.
	 * 
	 * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
	 */

    ThreadLocal<HashSet<String>> lockOwners = new ThreadLocal<HashSet<String>>();

    private DataSource dataSource;
    private String tableName;
    private String columnName;
    private String selectSql;
    private String insertSql;

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public <T> void executeWithLock(final String lockName, final Closure<T> closure, final T t) {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        DataSourceTransactionManager transactionManager = new DataSourceTransactionManager(this.getDataSource());
        TransactionStatus status = transactionManager.getTransaction(def);
        boolean transOwner = false;
        try {
            transOwner = this.obtainLock(lockName);
            closure.execute(t);
            try {
                transactionManager.commit(status);
            } catch (Exception ex) {
                retryExecuteWithLock(lockName, closure, t);
            }
        } catch (Exception ex) {
            getLog().error("executeWithLock: Exception {} rollback !!!", ex.getMessage());
            transactionManager.rollback(status);
        } finally {
            this.releaseLock(lockName, transOwner);
        }
    }

    protected <T> void retryExecuteWithLock(String lockName, final Closure<T> closure, final T t) {
        for (int retry = 1; retry <= 8; retry++) {
            try {
                executeWithLock(lockName, closure, t);
                return;
            } catch (Exception e) {
                getLog().error("retryExecuteWithLock: Exception " + e.getMessage(), e);
            }
            try {
                Thread.sleep(5000L); // retry every N seconds (the db connection
                // must be failed)
            } catch (InterruptedException e) {
                throw new IllegalStateException("Received interrupted exception", e);
            }
        }
        throw new IllegalStateException("JobStore is shutdown - aborting retry");
    }

    protected void releaseLock(String lockName, boolean doIt) {
        if (doIt) {
            try {
                this.releaseLock(lockName);
            } catch (Exception le) {
                getLog().error("Error returning lock: " + le.getMessage(), le);
            }
        }
    }

    /**
     * Grants a lock on the identified resource to the calling thread (blocking
     * until it is available).
     *
     * @return true if the lock was obtained.
     */
    public boolean obtainLock(String lockName) throws LockException {
        if (log.isDebugEnabled()) {
            log.debug("Lock '" + lockName + "' is desired by: " + Thread.currentThread().getName());
        }
        if (!isLockOwner(lockName)) {
            executeSQL(lockName);
            if (log.isDebugEnabled()) {
                log.debug("Lock '" + lockName + "' given to: " + Thread.currentThread().getName());
            }
            getThreadLocks().add(lockName);
        } else if (log.isDebugEnabled()) {
            log.debug("Lock '" + lockName + "' Is already owned by: " + Thread.currentThread().getName());
        }
        return true;
    }

    private void executeSQL(String lockName) throws LockException {
        Connection conn = DataSourceUtils.getConnection(getDataSource());
        PreparedStatement ps = null;
        ResultSet rs = null;
        SQLException initCause = null;
        // attempt lock two times (to work-around possible race conditions in
        // inserting the lock row the first time running)
        int count = 0;
        do {
            count++;
            try {
                ps = conn.prepareStatement(this.selectSql);
                ps.setString(1, lockName);
                if (getLog().isDebugEnabled()) {
                    getLog().debug("Lock '" + lockName + "' is being obtained: " + Thread.currentThread().getName());
                }
                rs = ps.executeQuery();
                if (!rs.next()) {
                    getLog().debug(
                            "Inserting new lock row for lock: '" + lockName + "' being obtained by thread: " + Thread
                                    .currentThread().getName());
                    rs.close();
                    rs = null;
                    ps.close();
                    ps = null;
                    ps = conn.prepareStatement(this.insertSql);
                    ps.setString(1, lockName);
                    int res = ps.executeUpdate();
                    if (res != 1) {
                        if (count < 3) {
                            // pause a bit to give another thread some time to
                            // commit the insert of the new lock row
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException ignore) {
                                Thread.currentThread().interrupt();
                            }
                            // try again ...
                            continue;
                        }
                        throw new SQLException(String.format(
                                "No row exists, and one could not be inserted in table {} for lock named: {}",
                                this.tableName, this.columnName));
                    }
                }
                return; // obtained lock, go
            } catch (SQLException sqle) {
                sqle.printStackTrace();
                if (initCause == null) {
                    initCause = sqle;
                }
                if (getLog().isDebugEnabled()) {
                    getLog().debug(
                            "Lock '" + lockName + "' was not obtained by: " + Thread.currentThread().getName() + (
                                    count < 3 ? " - will try again." : ""));
                }
                if (count < 3) {
                    // pause a bit to give another thread some time to commit
                    // the insert of the new lock row
                    try {
                        Thread.sleep(1000L);
                    } catch (InterruptedException ignore) {
                        Thread.currentThread().interrupt();
                    }
                    // try again ...
                    continue;
                }
                throw new LockException("Failure obtaining db row lock: " + sqle.getMessage(), sqle);
            } finally {
                if (rs != null) {
                    try {
                        rs.close();
                    } catch (Exception ignore) {
                    }
                }
                if (ps != null) {
                    try {
                        ps.close();
                    } catch (Exception ignore) {
                    }
                }
            }
        } while (count < 4);
        throw new LockException(
                "Failure obtaining db row lock, reached maximum number of attempts. Initial exception (if any) "
                        + "attached as root cause.",
                initCause);
    }

    private Logger getLog() {
        return this.log;
    }

    /**
     * Determine whether the calling thread owns a lock on the identified
     * resource.
     */
    public boolean isLockOwner(String lockName) {
        return getThreadLocks().contains(lockName);
    }

    /**
     * Release the lock on the identified resource if it is held by the calling
     * thread.
     */
    public void releaseLock(String lockName) {
        if (isLockOwner(lockName)) {
            if (getLog().isDebugEnabled()) {
                getLog().debug("Lock '{}'  returned by: {}", lockName, Thread.currentThread().getName());
            }
            getThreadLocks().remove(lockName);
        } else if (getLog().isDebugEnabled()) {
            getLog().warn("Lock '" + lockName + "' attempt to return by: " + Thread.currentThread().getName()
                    + " -- but not owner!", new Exception("stack-trace of wrongful returner"));
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        Validate.notNull(this.dataSource);
        Validate.notNull(this.tableName);
        Validate.notNull(this.columnName);
        this.selectSql = String.format(SELECT_FOR_LOCK, this.tableName, this.columnName);
        this.insertSql = String.format(INSERT_LOCK, this.tableName, this.columnName);
    }

    private HashSet<String> getThreadLocks() {
        HashSet<String> threadLocks = lockOwners.get();
        if (threadLocks == null) {
            threadLocks = new HashSet<String>();
            lockOwners.set(threadLocks);
        }
        return threadLocks;
    }

}
