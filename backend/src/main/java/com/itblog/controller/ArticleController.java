package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.dto.ArticlePublishRequest;
import com.itblog.entity.User;
import com.itblog.service.ArticleService;
import com.itblog.vo.ArticleDetailVO;
import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/article")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping("/list")
    public Result<PageVO<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "popular") String sort,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Long tagId) {
        return Result.ok(articleService.getArticleList(page, size, sort, categoryId, tagId, true));
    }

    @GetMapping("/search")
    public Result<PageVO<ArticleVO>> search(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam String keyword) {
        return Result.ok(articleService.searchArticles(page, size, keyword));
    }

    @GetMapping("/my")
    public Result<PageVO<ArticleVO>> myArticles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        return Result.ok(articleService.getMyArticles(page, size, user.getId(), true));
    }

    @GetMapping("/user/{userId}")
    public Result<PageVO<ArticleVO>> userArticles(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        boolean includePending = currentUser != null && currentUser.getId().equals(userId);
        return Result.ok(articleService.getMyArticles(page, size, userId, includePending));
    }

    @GetMapping("/drafts")
    public Result<PageVO<ArticleVO>> drafts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        return Result.ok(articleService.getDrafts(page, size, user.getId()));
    }

    @GetMapping("/{id}")
    public Result<ArticleDetailVO> detail(@PathVariable Long id, HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        Long userId = currentUser != null ? currentUser.getId() : null;
        return Result.ok(articleService.getArticleDetail(id, userId));
    }

    @PostMapping("/publish")
    public Result<Map<String, Long>> publish(@RequestBody ArticlePublishRequest request,
                                              HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        Long articleId = articleService.publishArticle(request, user.getId());
        return Result.ok(Map.of("id", articleId));
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody ArticlePublishRequest request,
                            HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        articleService.updateArticle(id, request, user.getId());
        return Result.ok();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        boolean isAdmin = user.getRole() == 1;
        articleService.deleteArticle(id, user.getId(), isAdmin);
        return Result.ok();
    }

    // ==== 管理员审核接口 ====

    @GetMapping("/pending")
    public Result<PageVO<ArticleVO>> pending(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        if (user.getRole() != 1) return Result.unauthorized("需要管理员权限");
        return Result.ok(articleService.getPendingArticles(page, size));
    }

    @PutMapping("/{id}/approve")
    public Result<?> approve(@PathVariable Long id, HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        if (user.getRole() != 1) return Result.unauthorized("需要管理员权限");
        articleService.approveArticle(id);
        return Result.ok();
    }

    @PutMapping("/{id}/reject")
    public Result<?> reject(@PathVariable Long id, HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        if (user.getRole() != 1) return Result.unauthorized("需要管理员权限");
        articleService.rejectArticle(id);
        return Result.ok();
    }
}
