package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.dto.CommentRequest;
import com.itblog.entity.User;
import com.itblog.service.CommentService;
import com.itblog.vo.CommentVO;
import com.itblog.vo.PageVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/page/{articleId}")
    public Result<PageVO<CommentVO>> topComments(
            @PathVariable Long articleId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size) {
        return Result.ok(commentService.getTopComments(page, size, articleId));
    }

    @GetMapping("/replies/{parentId}")
    public Result<List<CommentVO>> replies(@PathVariable Long parentId) {
        return Result.ok(commentService.getReplies(parentId));
    }

    @PostMapping
    public Result<Map<String, Long>> addComment(@RequestBody CommentRequest request,
                                                 HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        Long id = commentService.addComment(request, user.getId());
        return Result.ok(Map.of("id", id));
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id, HttpServletRequest httpReq) {
        User user = (User) httpReq.getAttribute("currentUser");
        if (user == null) return Result.unauthorized("请先登录");
        commentService.deleteComment(id, user.getId(), user.getRole() == 1);
        return Result.ok();
    }
}
