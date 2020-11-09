package com.chaoxing.activity.dto;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.pagehelper.Page;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;

/**分页bean
 * @className PageDTO
 * @description
 * @author chaoxing
 * @date 2018-04-25 12:09:08
 * @version ver 1.0
 */
public class PageDTO<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 默认的分页大小 */
    private static final Integer DEFAULT_PAGESIZE = 10;
    /** 默认的分页大小 */
    private static final Integer DEFAULT_PAGENUM = 1;
    /** 默认的pageNum名称 */
    private static final String DEFAULT_PAGENUM_NAME = "pageNum";
    private static final String DEFAULT_PAGESIZE_NAME = "pageSize";
    private static final Integer DEFAULT_NAVIGATEPAGES = 8;

    /** 分页请求url */
    @JSONField(serialize = false)
    private String searchUrl;
    /** 查询条件 */
    @JSONField(serialize = false)
    private Map<String, String> params;
    /** 数组查询条件 */
    @JSONField(serialize = false)
    private Map<String, List<String>> paramLists;

    //沿用PageInfo属性的定义
    /** 当前页 */
    private int pageNum;
    /** 每页的数量 */
    private int pageSize;
    /** 当前页的数量 */
    @JSONField(serialize = false)
    private int size;

    //由于startRow和endRow不常用，这里说个具体的用法
    //可以在页面中"显示startRow到endRow 共size条数据"

    /** 当前页面第一个元素在数据库中的行号 */
    @JSONField(serialize = false)
    private long startRow;
    /** 当前页面最后一个元素在数据库中的行号 */
    @JSONField(serialize = false)
    private long endRow;
    /** 总记录数 */
    private long total;
    /** 总页数 */
    private int pages;
    /** 结果集 */
    private List<T> list;

    /** 前一页 */
    @JSONField(serialize = false)
    private int prePage;
    /** 下一页 */
    @JSONField(serialize = false)
    private int nextPage;

    /** 是否为第一页 */
    @JSONField(serialize = false)
    private boolean firstPage = false;
    /** 是否为最后一页 */
    @JSONField(serialize = false)
    private boolean lastPage = false;
    /** 是否有前一页 */
    @JSONField(serialize = false)
    private boolean hasPreviousPage = false;
    /** 是否有下一页 */
    @JSONField(serialize = false)
    private boolean hasNextPage = false;
    /** 导航页码数 */
    @JSONField(serialize = false)
    private int navigatePages;
    /** 所有导航页号 */
    @JSONField(serialize = false)
    private int[] navigatepageNums;
    /** 导航条上的第一页 */
    @JSONField(serialize = false)
    private int navigateFirstPage;
    /** 导航条上的最后一页 */
    @JSONField(serialize = false)
    private int navigateLastPage;

    public static <T> PageDTO<T> buildFromRequest(HttpServletRequest request) {
        PageDTO<T> pageBean = new PageDTO<T>();
        String uri = request.getRequestURI();
        pageBean.setSearchUrl(uri);
        // 遍历 request.getParameterMap() 提取请求参数
        Set<String> parameterKeys = request.getParameterMap().keySet();
        for (Object key : parameterKeys){
            String[] args = request.getParameterValues(key.toString());
            if (args.length>1){
                pageBean.getParamLists().put(key.toString(), convertParamArr(args));
            }else{
                pageBean.getParams().put(key.toString(), request.getParameter(key.toString()));
            }
        }
        String pageSize = pageBean.getParams().get(DEFAULT_PAGESIZE_NAME);
        if (!StringUtils.isEmpty(pageSize)) {
            pageBean.setPageSize(Integer.valueOf(pageSize));
        } else {
            pageBean.setPageSize(DEFAULT_PAGESIZE);
        }
        String strPageNo = pageBean.getParams().get(DEFAULT_PAGENUM_NAME);
        if (strPageNo == null){
            pageBean.getParams().put(DEFAULT_PAGENUM_NAME, String.valueOf(DEFAULT_PAGENUM));
        }else{
            // 输入页号需要做check
            try{
                strPageNo = strPageNo == null? strPageNo : strPageNo.trim();
                int iPageNo = Integer.parseInt(strPageNo);
                pageBean.setPageNum( iPageNo < 1 ? 1 : iPageNo );
            }catch(NumberFormatException e){
                pageBean.getParams().put(DEFAULT_PAGENUM_NAME, String.valueOf(DEFAULT_PAGENUM));
            }
            // 点击分页标签时的处理，编辑isFirst，totalRecord 项目
            pageBean.setFirstPage(false);
        }
        return pageBean;
    }

    private static List<String> convertParamArr(String[] params) {
        List<String> list = new ArrayList<>();
        if (params != null) {
            for (String param : params) {
                if (!list.contains(param)) {
                    list.add(param);
                }
            }
        }
        return list;
    }


    public void pagination(List<T> list) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();

            this.pages = page.getPages();
            this.list = page;
            this.size = page.size();
            this.total = page.getTotal();
            //由于结果是>startRow的，所以实际的需要+1
            if (this.size == 0) {
                this.startRow = 0;
                this.endRow = 0;
            } else {
                this.startRow = page.getStartRow() + 1;
                //计算实际的endRow（最后一页的时候特殊）
                this.endRow = this.startRow - 1 + this.size;
            }
        } else if (list instanceof Collection) {
            this.pageNum = 1;
            this.pageSize = list.size();

            this.pages = this.pageSize > 0 ? 1 : 0;
            this.list = list;
            this.size = list.size();
            this.total = list.size();
            this.startRow = 0;
            this.endRow = list.size() > 0 ? list.size() - 1 : 0;
        }
        if (list instanceof Collection) {
            //计算导航页
            calcNavigatepageNums();
            //计算前后页，第一页，最后一页
            calcPage();
            //判断页面边界
            judgePageBoudary();
        }
    }

    private PageDTO() {
        pageNum = 1;
        pageSize = DEFAULT_PAGESIZE;
        total = 0;
        pages = 0;
        params = new HashMap<>();
        paramLists = new HashMap<>();
        searchUrl = "";
        navigatePages = DEFAULT_NAVIGATEPAGES;
        navigatepageNums = null;
        firstPage = true;
    }

    /**
     * 包装Page对象
     * @param list
     */
    private PageDTO(List<T> list) {
        this(list, 8);
    }

    /**
     * 包装Page对象
     * @param list page结果
     * @param navigatePages 页码数量
     */
    private PageDTO(List<T> list, int navigatePages) {
        if (list instanceof Page) {
            Page page = (Page) list;
            this.pageNum = page.getPageNum();
            this.pageSize = page.getPageSize();

            this.pages = page.getPages();
            this.list = page;
            this.size = page.size();
            this.total = page.getTotal();
            //由于结果是>startRow的，所以实际的需要+1
            if (this.size == 0) {
                this.startRow = 0;
                this.endRow = 0;
            } else {
                this.startRow = page.getStartRow() + 1;
                //计算实际的endRow（最后一页的时候特殊）
                this.endRow = this.startRow - 1 + this.size;
            }
        } else if (list instanceof Collection) {
            this.pageNum = 1;
            this.pageSize = list.size();

            this.pages = this.pageSize > 0 ? 1 : 0;
            this.list = list;
            this.size = list.size();
            this.total = list.size();
            this.startRow = 0;
            this.endRow = list.size() > 0 ? list.size() - 1 : 0;
        }
        if (list instanceof Collection) {
            this.navigatePages = navigatePages;
            //计算导航页
            calcNavigatepageNums();
            //计算前后页，第一页，最后一页
            calcPage();
            //判断页面边界
            judgePageBoudary();
        }
    }

    /**
     * 计算导航页
     */
    private void calcNavigatepageNums() {
        //当总页数小于或等于导航页码数时
        if (pages <= navigatePages) {
            navigatepageNums = new int[pages];
            for (int i = 0; i < pages; i++) {
                navigatepageNums[i] = i + 1;
            }
        } else { //当总页数大于导航页码数时
            navigatepageNums = new int[navigatePages];
            int startNum = pageNum - navigatePages / 2;
            int endNum = pageNum + navigatePages / 2;

            if (startNum < 1) {
                startNum = 1;
                //(最前navigatePages页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            } else if (endNum > pages) {
                endNum = pages;
                //最后navigatePages页
                for (int i = navigatePages - 1; i >= 0; i--) {
                    navigatepageNums[i] = endNum--;
                }
            } else {
                //所有中间页
                for (int i = 0; i < navigatePages; i++) {
                    navigatepageNums[i] = startNum++;
                }
            }
        }
    }

    /**
     * 计算前后页，第一页，最后一页
     */
    private void calcPage() {
        if (navigatepageNums != null && navigatepageNums.length > 0) {
            navigateFirstPage = navigatepageNums[0];
            navigateLastPage = navigatepageNums[navigatepageNums.length - 1];
            if (pageNum > 1) {
                prePage = pageNum - 1;
            }
            if (pageNum < pages) {
                nextPage = pageNum + 1;
            }
        }
    }

    /**
     * 判定页面边界
     */
    private void judgePageBoudary() {
        firstPage = pageNum == 1;
        lastPage = pageNum == pages || pages == 0;
        hasPreviousPage = pageNum > 1;
        hasNextPage = pageNum < pages;
    }

    public String getSearchUrl() {
        return searchUrl;
    }

    public void setSearchUrl(String searchUrl) {
        this.searchUrl = searchUrl;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public Map<String, List<String>> getParamLists() {
        return paramLists;
    }

    public void setParamLists(Map<String, List<String>> paramLists) {
        this.paramLists = paramLists;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public long getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPages() {
        return pages;
    }

    public void setPages(int pages) {
        this.pages = pages;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }

    @Deprecated
    /** firstPage就是1, 此函数获取的是导航条上的第一页, 容易产生歧义 */
    public int getFirstPage() {
        return navigateFirstPage;
    }

    @Deprecated
    public void setFirstPage(int firstPage) {
        this.navigateFirstPage = firstPage;
    }

    public int getPrePage() {
        return prePage;
    }

    public void setPrePage(int prePage) {
        this.prePage = prePage;
    }

    public int getNextPage() {
        return nextPage;
    }

    public void setNextPage(int nextPage) {
        this.nextPage = nextPage;
    }

    @Deprecated
    /** 请用getPages()来获取最后一页, 此函数获取的是导航条上的最后一页, 容易产生歧义. */
    public int getLastPage() {
        return navigateLastPage;
    }

    @Deprecated
    public void setLastPage(int lastPage) {
        this.navigateLastPage = lastPage;
    }

    public boolean isFirstPage() {
        return firstPage;
    }

    public void setFirstPage(boolean firstPage) {
        this.firstPage = firstPage;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public boolean isHasPreviousPage() {
        return hasPreviousPage;
    }

    public void setHasPreviousPage(boolean hasPreviousPage) {
        this.hasPreviousPage = hasPreviousPage;
    }

    public boolean isHasNextPage() {
        return hasNextPage;
    }

    public void setHasNextPage(boolean hasNextPage) {
        this.hasNextPage = hasNextPage;
    }

    public int getNavigatePages() {
        return navigatePages;
    }

    public void setNavigatePages(int navigatePages) {
        this.navigatePages = navigatePages;
    }

    public int[] getNavigatepageNums() {
        return navigatepageNums;
    }

    public void setNavigatepageNums(int[] navigatepageNums) {
        this.navigatepageNums = navigatepageNums;
    }

    public int getNavigateFirstPage() {
        return navigateFirstPage;
    }

    public int getNavigateLastPage() {
        return navigateLastPage;
    }

    public void setNavigateFirstPage(int navigateFirstPage) {
        this.navigateFirstPage = navigateFirstPage;
    }

    public void setNavigateLastPage(int navigateLastPage) {
        this.navigateLastPage = navigateLastPage;
    }

}