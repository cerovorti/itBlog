package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.User;
import com.itblog.service.FavoriteService;
import com.itblog.vo.ArticleVO;
import com.itblog.vo.PageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    private final FavoriteService favoriteService;
    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/toggle/{articleId}")
    public Result<Map<String, String>> toggle(@PathVariable Long articleId,
                                               HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        favoriteService.toggleFavorite(articleId, user.getId());
        boolean isFav = favoriteService.isFavorited(articleId, user.getId());
        return Result.ok(Map.of("status", isFav ? "favorited" : "unfavorited"));
    }

    @GetMapping("/status/{articleId}")
    public Result<Map<String, Boolean>> status(@PathVariable Long articleId,
                                                HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.ok(Map.of("favorited", false));
        return Result.ok(Map.of("favorited", favoriteService.isFavorited(articleId, user.getId())));
    }

    @GetMapping("/list")
    public Result<PageVO<ArticleVO>> list(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        return Result.ok(favoriteService.getFavoriteArticles(page, size, user.getId()));
    }
}
