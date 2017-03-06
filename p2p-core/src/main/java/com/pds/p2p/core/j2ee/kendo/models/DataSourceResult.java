package com.pds.p2p.core.j2ee.kendo.models;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.github.pagehelper.Page;

public class DataSourceResult<T> {
    private long total;

    private List<T> data;

    private Map<String, Object> aggregates;

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public Map<String, Object> getAggregates() {
        return aggregates;
    }

    public void setAggregates(Map<String, Object> aggregates) {
        this.aggregates = aggregates;
    }

    public DataSourceResult(List<T> list) {
        if (list instanceof Page) {
            Page<T> page = (Page<T>) list;
            this.data = page;
            this.total = page.getTotal();
        } else if (list instanceof Collection) {
            this.data = list;
            this.total = list.size();
        }
    }

    public DataSourceResult() {

    }
}
