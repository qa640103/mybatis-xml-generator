package com.yn.query;

import com.alibaba.fastjson2.JSON;
import com.github.pagehelper.PageHelper;

import java.io.Serializable;
import java.util.Map;

/**
 * 分页查询
 */
public class PageQuery implements Serializable {

    private Integer current;

    private Integer pageSize;

    private String sort;

    private String orderField;

    private String orderType;

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        this.current = current;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public String getOrderField() {
        return orderField;
    }

    public void setOrderField(String orderField) {
        this.orderField = orderField;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    /**
     * 设置分页信息
     * 在mapper执行查询前调用
     */
    public void setPageInfo(){
        if (this.current != null && this.pageSize != null){
            PageHelper.startPage(this.current, this.pageSize);
        }
    }

    /**
     * 处理排序字段
     */
    public void handleSortMap(){
        if (this.sort != null && this.sort.length() > 0){
            try {
                Map<String, String> sortMap = JSON.parseObject(this.sort, Map.class);
                for (Map.Entry<String, String> entry : sortMap.entrySet()) {
                    this.orderField = entry.getKey();
                    if ("ascend".equals(entry.getValue())){
                        this.orderType = "asc";
                    } else if ("descend".equals(entry.getValue())) {
                        this.orderType = "desc";
                    }
                    break;
                }
            }catch (Exception ignored){}
        }
    }
}
