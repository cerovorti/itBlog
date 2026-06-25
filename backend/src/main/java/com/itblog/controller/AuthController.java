package com.itblog.controller;

import com.itblog.common.Result;
import com.itblog.dto.LoginRequest;
import com.itblog.dto.RegisterRequest;
import com.itblog.service.UserService;
import com.itblog.vo.UserVO;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public Result<UserVO> register(@RequestBody RegisterRequest request) {
        return Result.ok(userService.register(request));
    }

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody LoginRequest request) {
        Map<String, Object> result = userService.login(request);
        return Result.ok(result);
    }
}
