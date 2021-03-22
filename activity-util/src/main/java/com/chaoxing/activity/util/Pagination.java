package com.chaoxing.activity.util;

/**
 * @author wangxin
 * @time 2019/5/23 15:32
 */
public class Pagination {

    private Integer page = 1;

    private Integer pageSize = 10;

    /**
     * 搜索使用
     */
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public static Pagination of(Integer page, Integer pageSize){
        Pagination p = new Pagination();
        p.page = page;
        p.pageSize = pageSize;
        return p;
    }
}
