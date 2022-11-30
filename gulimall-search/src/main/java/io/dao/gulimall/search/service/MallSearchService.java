package io.dao.gulimall.search.service;

import io.dao.gulimall.search.vo.SearchParam;
import io.dao.gulimall.search.vo.SearchResult;

public interface MallSearchService {
    // 检索的所有参数
    // 返回检索的结果
    SearchResult search(SearchParam searchParam);
}
