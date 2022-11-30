package io.dao.gulimall.search.controller;

import io.dao.common.utils.Query;
import io.dao.gulimall.search.service.MallSearchService;
import io.dao.gulimall.search.vo.SearchParam;
import io.dao.gulimall.search.vo.SearchResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    @GetMapping({"/list.html"})
    public String listPage(SearchParam searchParam, Model model, HttpServletRequest request) {
        // 根据传递来的页面的查询参数，去es中检索商品
        String queryString = request.getQueryString();
        searchParam.set_queryString(queryString);
        SearchResult result = mallSearchService.search(searchParam);
        model.addAttribute("result", result);
        return "list";
    }

}
