package com.itblog.service;

import com.itblog.dto.LoginRequest;
import com.itblog.dto.RegisterRequest;
import com.itblog.entity.User;
import com.itblog.vo.UserVO;

import java.util.List;
import java.util.Map;

public interface UserService {
    UserVO register(RegisterRequest request);
    Map<String, Object> login(LoginRequest request);
    UserVO getProfile(Long userId);
    UserVO getCurrentUser(Long userId);
    void updateProfile(Long userId, String avatar, String bio, String skills);
    User getById(Long userId);
    List<UserVO> getRecommendedAuthors();
    List<UserVO> getAllUsers(int page, int size);
    long countAllUsers();
    void setRecommended(Long userId, boolean recommended);
    void banUser(Long userId, boolean ban, Integer hours, boolean permanent);
}
