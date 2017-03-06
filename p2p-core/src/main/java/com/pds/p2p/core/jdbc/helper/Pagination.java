package com.pds.p2p.core.jdbc.helper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.RowBounds;

import com.google.common.collect.Lists;

public class Pagination extends RowBounds {
    public static void main(String[] args) {
        System.out.println(Pagination.createByNumPerpageAndTotal(100, 201).ranges());
    }

    private int totalCount;
    private String sortField;
    private String sortOrder;

    // 因为父类的offset和limit在运行期间将被替换掉,所以在此记录初始值
    private int myOffset;
    private int mylimit;
    private boolean proceed;
    private boolean changeCountSql = true;

    public boolean isChangeCountSql() {
        return changeCountSql;
    }

    public void setChangeCountSql(boolean changeCountSql) {
        this.changeCountSql = changeCountSql;
    }

    private static String PAGE = "page";
    private static String PAGE_SIZE = "pageSize";
    private static String SORT_FIELD = "sortField";
    private static String SORT_ORDER = "sortOrder";

    public boolean isProceed() {
        return proceed;
    }

    public void setProceed(boolean proceed) {
        this.proceed = proceed;
    }

    static public Pagination DEFAULT = new Pagination();

    public static Pagination create(Map<String, Object> parms) {
        String page = (String) parms.get(PAGE);
        if (StringUtils.isBlank(page)) {
            return Pagination.DEFAULT;
        }
        // 分页
        int currentPage = Integer.parseInt(page);
        int numPerPage = Integer.parseInt((String) parms.get(PAGE_SIZE));
        // 字段排序
        String sortField = (String) parms.get(SORT_FIELD);
        String sortOrder = (String) parms.get(SORT_ORDER);

        Pagination pagination = Pagination.create(currentPage, numPerPage);
        pagination.setSortField(sortField);
        pagination.setSortOrder(sortOrder);
        return pagination;
    }

    /**
     * 页按1做基数
     *
     * @param currentPage
     * @param numPerPage
     *
     * @return
     */
    public static Pagination create(int currentPage, int numPerPage) {
        int offset = (currentPage - 1) * numPerPage;
        int limit = numPerPage;
        Pagination pagination = new Pagination(offset, limit);
        return pagination;
    }

    public static Pagination createByNumPerpageAndTotal(int numPerPage, int totalCount) {
        int limit = numPerPage;
        Pagination pagination = new Pagination(0, limit);
        pagination.setTotalCount(totalCount);
        return pagination;
    }

    public <T> List<List<T>> shardList(List<T> largeList) {
        List<Range<Integer>> rs = this.ranges();
        List<List<T>> result = Lists.newArrayList();
        for (Range<Integer> r : rs) {
            List<T> sublist = largeList.subList(r.getMinimum(), r.getMaximum());
            result.add(sublist);
        }
        return result;
    }

    public Pagination() {
        super();
        init();
    }

    public Pagination(int offset, int limit) {
        super(offset, limit);
        init();
    }

    protected void init() {
        this.proceed = true;
        this.myOffset = this.getOffset();
        this.mylimit = this.getLimit();
    }

    public int getMyOffset() {
        return myOffset;
    }

    public int getMylimit() {
        return mylimit;
    }

    public boolean isNotLimit() {
        return this.mylimit == Integer.MAX_VALUE && this.myOffset == 0;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public int getPageNum() {
        return (int) Math.ceil((double) totalCount / this.getMylimit());
    }

    public int getNumPerPage() {
        return this.getMylimit();
    }

    public int whichPage(int offset) {
        return (int) Math.ceil((double) (offset + 1) / getNumPerPage());
    }

    public List<Range<Integer>> ranges() {
        return ranges(Integer.MAX_VALUE);

    }

    public List<Range<Integer>> ranges(int max) {
        int n = this.totalCount / this.getMylimit();
        List<Range<Integer>> result = Lists.newArrayList();
        if (n == 0) {
            result.add(Range.between(0, this.totalCount));
        } else {
            int a = 0;
            int b = 0;
            for (int i = 0; i < n; ++i) {
                a = i * this.getMylimit();
                b = a + this.getMylimit();
                Range<Integer> range = Range.between(a, b);
                result.add(range);
                if (result.size() == max) {
                    return result;
                }
            }
            int m = this.totalCount % this.getMylimit();
            if (m > 0 && result.size() < max) {
                result.add(Range.between(b, b + m));
            }
        }
        return result;

    }

    /***
     * 提供分区：[),左闭右开区间
     *
     * @param totalCount
     * @param numberOfPage
     *
     * @return
     */
    static public List<Range<Integer>> ranges(int totalCount, int numberOfPage) {
        int n = totalCount / numberOfPage;
        List<Range<Integer>> result = Lists.newArrayList();
        if (n == 0) {
            result.add(Range.between(0, totalCount));
        } else {
            int a = 0;
            int b = 0;
            for (int i = 0; i < n; ++i) {
                a = i * numberOfPage;
                b = a + numberOfPage;
                Range<Integer> range = Range.between(a, b);
                result.add(range);
            }
            int m = totalCount % numberOfPage;
            if (m > 0) {
                result.add(Range.between(b, b + m));
            }
        }
        return result;
    }

}
