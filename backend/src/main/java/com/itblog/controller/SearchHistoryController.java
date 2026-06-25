package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.User;
import com.itblog.service.SearchHistoryService;
import com.itblog.vo.SearchHistoryVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchHistoryController {

    private final SearchHistoryService searchHistoryService;

    public SearchHistoryController(SearchHistoryService searchHistoryService) {
        this.searchHistoryService = searchHistoryService;
    }

    @PostMapping("/history")
    public Result<Void> recordSearch(@RequestParam String keyword,
                                     HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.ok(); // 未登录不记录
        searchHistoryService.recordSearch(keyword, user.getId());
        return Result.ok();
    }

    @GetMapping("/history")
    public Result<List<SearchHistoryVO>> getHistory(HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        return Result.ok(searchHistoryService.getUserHistory(user.getId()));
    }

    @DeleteMapping("/history")
    public Result<Void> clearHistory(HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        searchHistoryService.clearHistory(user.getId());
        return Result.ok();
    }

    @DeleteMapping("/history/{id}")
    public Result<Void> deleteHistoryItem(@PathVariable Long id,
                                           HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        searchHistoryService.deleteHistoryItem(id, user.getId());
        return Result.ok();
    }
}