package com.tree.treeso.datasource;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @description 统一数据源接口(新接入数据源必须实现)
 * @author tree
 * @version 1.0
 * @date 2023/6/23 23:00
 */
public interface DataSource<T> {
    /**
     * 搜索
     * @param searchText 搜索文本
     * @param pageNum 页码
     * @param pageSize 页面数据量
     * @return
     */
    Page<T> doSearch(String searchText, long pageNum, long pageSize);
}
