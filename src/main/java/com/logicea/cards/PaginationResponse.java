package com.logicea.cards;

import java.util.List;

public class PaginationResponse<T> {
    private int page;
    private int size;
    private String sort;
    private int total;
    private List<T> data;

    public PaginationResponse(int page, int size, String sort, int totalPages, List<T> data) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.total = totalPages;
        this.data = data;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }


}
