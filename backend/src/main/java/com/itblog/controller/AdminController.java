package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.User;
import com.itblog.service.*;
import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;
import com.itblog.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final ArticleService articleService;
    private final CategoryService categoryService;
    private final TagService tagService;
    private final CommentService commentService;
    private final UserService userService;

    public AdminController(ArticleService articleService, CategoryService categoryService,
                           TagService tagService, CommentService commentService,
                           UserService userService) {
        this.articleService = articleService;
        this.categoryService = categoryService;
        this.tagService = tagService;
        this.commentService = commentService;
        this.userService = userService;
    }

    private boolean checkAdmin(HttpServletRequest request) {
        User user = (User) request.getAttribute("currentUser");
        return user != null && user.getRole() == 1;
    }

    @GetMapping("/dashboard")
    public Result<Map<String, Object>> dashboard(HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        // 统计文章数
        PageVO<ArticleVO> allArticles = articleService.getArticleList(1, 1000, "latest", null, null, false);
        long totalArticles = allArticles.getTotal();
        long publishedArticles = 0;
        long draftArticles = 0;
        for (ArticleVO a : allArticles.getRecords()) {
            if (a.getStatus() == 1) publishedArticles++;
            else draftArticles++;
        }
        // 用户数
        long totalUsers = userService.countAllUsers();

        // 最近5篇文章
        PageVO<ArticleVO> recent = articleService.getArticleList(1, 5, "latest", null, null, false);

        return Result.ok(Map.of(
                "totalArticles", totalArticles,
                "publishedArticles", publishedArticles,
                "draftArticles", draftArticles,
                "totalUsers", totalUsers,
                "recentArticles", recent.getRecords()
        ));
    }

    @GetMapping("/articles")
    public Result<PageVO<ArticleVO>> articles(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) Integer status,
            HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        // 不传 status 时显示全部文章（含草稿）
        return Result.ok(articleService.getArticleList(page, size, "latest", null, null, false));
    }

    @PutMapping("/article/{id}/status")
    public Result<?> updateArticleStatus(@PathVariable Long id,
                                          @RequestBody Map<String, Integer> body,
                                          HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        articleService.updateStatus(id, body.get("status"));
        return Result.ok();
    }

    @DeleteMapping("/comment/{id}")
    public Result<?> deleteComment(@PathVariable Long id, HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        commentService.deleteComment(id, 0L, true);
        return Result.ok();
    }

    @PostMapping("/category")
    public Result<?> addCategory(@RequestBody Map<String, Object> body,
                                  HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        String name = (String) body.get("name");
        Long parentId = body.get("parentId") != null ?
                ((Number) body.get("parentId")).longValue() : 0L;
        categoryService.addCategory(name, parentId);
        return Result.ok();
    }

    @DeleteMapping("/category/{id}")
    public Result<?> deleteCategory(@PathVariable Long id, HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        categoryService.deleteCategory(id);
        return Result.ok();
    }

    @DeleteMapping("/tag/{id}")
    public Result<?> deleteTag(@PathVariable Long id, HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        tagService.deleteTag(id);
        return Result.ok();
    }

    @GetMapping("/users")
    public Result<List<UserVO>> users(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "50") int size,
            HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        return Result.ok(userService.getAllUsers(page, size));
    }

    @PutMapping("/user/{id}/recommend")
    public Result<?> setUserRecommend(@PathVariable Long id,
                                       @RequestBody Map<String, Boolean> body,
                                       HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        userService.setRecommended(id, body.getOrDefault("recommended", true));
        return Result.ok();
    }

    @PutMapping("/user/{id}/ban")
    public Result<?> banUser(@PathVariable Long id,
                              @RequestBody Map<String, Object> body,
                              HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        boolean ban = body.containsKey("ban") && (Boolean) body.get("ban");
        Integer hours = body.containsKey("hours") ? ((Number) body.get("hours")).intValue() : null;
        boolean permanent = body.containsKey("permanent") && (Boolean) body.get("permanent");
        userService.banUser(id, ban, hours, permanent);
        return Result.ok();
    }

    @PutMapping("/user/{id}/password")
    public Result<?> resetUserPassword(@PathVariable Long id,
                                        @RequestBody Map<String, String> body,
                                        HttpServletRequest request) {
        if (!checkAdmin(request)) return Result.forbidden("需要管理员权限");
        String newPassword = body.get("newPassword");
        if (newPassword == null || newPassword.isBlank()) return Result.fail("新密码不能为空");
        if (newPassword.length() < 6) return Result.fail("新密码长度不能少于6位");
        userService.adminResetPassword(id, newPassword);
        return Result.ok();
    }
}
