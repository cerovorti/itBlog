package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.entity.User;
import com.itblog.service.UserService;
import com.itblog.vo.UserVO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/profile/{userId}")
    public Result<UserVO> getProfile(@PathVariable Long userId) {
        return Result.ok(userService.getProfile(userId));
    }

    @GetMapping("/profile")
    public Result<UserVO> getMyProfile(HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) return Result.unauthorized("请先登录");
        return Result.ok(userService.getCurrentUser(currentUser.getId()));
    }

    @PutMapping("/profile")
    public Result<?> updateProfile(@RequestBody Map<String, String> body,
                                   HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) return Result.unauthorized("请先登录");
        userService.updateProfile(currentUser.getId(),
                body.get("avatar"), body.get("bio"), body.get("skills"));
        return Result.ok();
    }

    @GetMapping("/recommended-authors")
    public Result<List<UserVO>> recommendedAuthors() {
        return Result.ok(userService.getRecommendedAuthors());
    }
}
