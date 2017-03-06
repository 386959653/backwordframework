package com.pds.p2p.core.jdbc.pk;

import javax.sql.DataSource;

import com.pds.p2p.core.jdbc.helper.JdbcHelper;
import org.apache.ibatis.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.incrementer.AbstractDataFieldMaxValueIncrementer;

public class SequenceGenerator extends AbstractDataFieldMaxValueIncrementer {
    private static String SELECT_SEQ = "select name,nextid,increment,padding,prefix from %s where name=?";
    private static String UPDATE_SEQ = "update %s set nextid=? where name=? and nextid=?";
    private static String INSERT_SEQ = "insert into %s (name,nextid,increment,padding,prefix) value (?, ?, ?, ?, ?)";

    private long maxID;
    private long currentID = 0l;
    private JdbcHelper jdbcHelper;

    private int increment = 5;

    private String selectSql;
    private String updateSql;
    private String insertSql;

    /**
     * The name of the rowId for this sequence
     */
    private String rowId;
    private String prefix;

    public SequenceGenerator() {
        this.maxID = 0L;
        this.currentID = 0L;
    }

    public SequenceGenerator(DataSource dataSource, String incrementerName, String rowId, int increment) {
        super(dataSource, incrementerName);
        this.maxID = 0L;
        this.currentID = 0L;
        this.increment = increment;
        this.jdbcHelper = new JdbcHelper(new JdbcTemplate(dataSource));
        this.setRowId(rowId);
        this.setIncrementerName(incrementerName);
    }

    @Override
    public void setDataSource(DataSource dataSource) {
        super.setDataSource(dataSource);
        this.jdbcHelper = new JdbcHelper(new JdbcTemplate(dataSource));
    }

    @Override
    public void setIncrementerName(String incrementerName) {
        super.setIncrementerName(incrementerName);
        this.selectSql = String.format(SELECT_SEQ, incrementerName);
        this.updateSql = String.format(UPDATE_SEQ, incrementerName);
        this.insertSql = String.format(INSERT_SEQ, incrementerName);
    }

    @Override
    protected long getNextKey() {
        return nextUniqueID();
    }

    @Override
    public String nextStringValue() throws DataAccessException {
        String s = Long.toString(getNextKey());
        int len = s.length() + this.prefix.length();
        if (len < this.paddingLength) {
            StringBuilder sb = new StringBuilder(this.paddingLength);
            sb.append(this.prefix);
            for (int i = 0; i < this.paddingLength - len; i++) {
                sb.append('0');
            }
            sb.append(s);
            s = sb.toString();
        }
        return s;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    /**
     * Returns the next available unique ID. Essentially this provides for the
     * functionality of an auto-increment database field.
     */
    public synchronized long nextUniqueID() {
        if (!(currentID < maxID)) {
            // Get next block -- make 5 attempts at maximum.
            getNextBlock(5);
        }
        long id = currentID;
        currentID++;
        return id;
    }

    private void getNextBlock(int count) {
        if (count == 0) {
            LogFactory.getLog(SequenceGenerator.class).error("Failed at last attempt to obtain an ID, aborting...");
            return;
        }
        boolean success = false;
        try {

            Sequence sequence = this.jdbcHelper.directQueryForObject(Sequence.class, this.selectSql, this.rowId);
            if (sequence == null) {
                sequence = new Sequence(this.increment);
                sequence.setNextid(1L);
                sequence.setName(this.rowId);
                this.jdbcHelper.directExecuteUpdate(this.insertSql, //
                        sequence.getName(), //
                        sequence.getNextid(), //
                        sequence.getIncrement(), sequence.getPadding(), sequence.getPrefix());
            }
            this.prefix = sequence.getPrefix();
            this.currentID = sequence.getNextid();
            long newID = this.currentID + sequence.getIncrement();
            sequence.setCurrid(currentID);
            sequence.setNextid(newID);
            int occurs = this.jdbcHelper.directExecuteUpdate(this.updateSql,//
                    sequence.getNextid(), //
                    sequence.getName(), //
                    sequence.getCurrid());//

            this.setPaddingLength(sequence.getPadding());

            success = occurs == 1;
            if (success) {
                this.maxID = newID;
            }
        } catch (Exception ex) {
            LogFactory.getLog(SequenceGenerator.class).error(ex.getMessage());
        } finally {
        }
        if (!success) {
            LogFactory.getLog(SequenceGenerator.class)
                    .error("WARNING: failed to obtain next ID block due to thread contention. Trying again...");
            // Call this method again, but sleep briefly to try to avoid thread
            // contention.
            try {
                Thread.sleep(75);
            } catch (InterruptedException ie) {
            }
            getNextBlock(count - 1);
        }
    }

}
