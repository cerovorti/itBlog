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

    @PutMapping("/password")
    public Result<?> changePassword(@RequestBody Map<String, String> body,
                                    HttpServletRequest request) {
        User currentUser = (User) request.getAttribute("currentUser");
        if (currentUser == null) return Result.unauthorized("请先登录");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        if (oldPassword == null || oldPassword.isBlank()) return Result.fail("原密码不能为空");
        if (newPassword == null || newPassword.isBlank()) return Result.fail("新密码不能为空");
        if (newPassword.length() < 6) return Result.fail("新密码长度不能少于6位");
        userService.changePassword(currentUser.getId(), oldPassword, newPassword);
        return Result.ok();
    }
}
