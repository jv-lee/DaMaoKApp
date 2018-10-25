package com.yuan7.lockscreen.model.entity;

import java.util.List;

/**
 * Created by Administrator on 2018/5/25.
 */

public class Data<T> {
    private Integer page;
    private Integer countPage;
    private Integer pageNumber;
    private Integer total;
    private List<T> rows;

    public Data() {
    }

    public Data(Integer page, Integer countPage, Integer pageNumber, Integer total, List<T> rows) {
        this.page = page;
        this.countPage = countPage;
        this.pageNumber = pageNumber;
        this.total = total;
        this.rows = rows;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getCountPage() {
        return countPage;
    }

    public void setCountPage(Integer countPage) {
        this.countPage = countPage;
    }

    public Integer getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(Integer pageNumber) {
        this.pageNumber = pageNumber;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
