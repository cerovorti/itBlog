package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.User;
import com.itblog.service.LikeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/like")
public class LikeController {

    private final LikeService likeService;
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }


    @PostMapping("/toggle/{articleId}")
    public Result<Map<String, Object>> toggle(@PathVariable Long articleId,
                                               HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        return Result.ok(likeService.toggleLike(articleId, user.getId()));
    }
}

