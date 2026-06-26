package com.itblog.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itblog.common.BusinessException;
import com.itblog.dto.LoginRequest;
import com.itblog.dto.RegisterRequest;
import com.itblog.entity.User;
import com.itblog.mapper.UserMapper;
import com.itblog.mapper.ArticleMapper;
import com.itblog.security.JwtUtils;
import com.itblog.service.UserService;
import com.itblog.vo.UserVO;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final ArticleMapper articleMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public UserServiceImpl(UserMapper userMapper, ArticleMapper articleMapper, PasswordEncoder passwordEncoder, JwtUtils jwtUtils) {
        this.userMapper = userMapper;
        this.articleMapper = articleMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @Override
    public UserVO register(RegisterRequest request) {
        LambdaQueryWrapper<User> uw = new LambdaQueryWrapper<>();
        uw.eq(User::getUsername, request.getUsername());
        if (userMapper.selectCount(uw) > 0) {
            throw new BusinessException("Username already exists");
        }
        LambdaQueryWrapper<User> ew = new LambdaQueryWrapper<>();
        ew.eq(User::getEmail, request.getEmail());
        if (userMapper.selectCount(ew) > 0) {
            throw new BusinessException("Email already registered");
        }
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRole(0);
        userMapper.insert(user);
        return toVO(user);
    }

    @Override
    public Map<String, Object> login(LoginRequest request) {
        LambdaQueryWrapper<User> w = new LambdaQueryWrapper<>();
        w.eq(User::getUsername, request.getUsername());
        User user = userMapper.selectOne(w);
        if (user == null || !passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new BusinessException(401, "Invalid username or password");
        }
        boolean rememberMe = request.getRememberMe() != null && request.getRememberMe();
        if (user.getStatus() != null && user.getStatus() == 0) {
            String msg = "账号已被封禁";
            if (user.getBanUntil() == null) {
                msg += "（永久封禁）";
            } else {
                Duration d = Duration.between(LocalDateTime.now(), user.getBanUntil());
                long totalMinutes = d.toMinutes();
                if (totalMinutes > 0) {
                    if (totalMinutes < 60) {
                        msg += "，剩余 " + totalMinutes + " 分钟";
                    } else if (totalMinutes < 1440) {
                        msg += "，剩余 " + (totalMinutes / 60) + " 小时 " + (totalMinutes % 60) + " 分钟";
                    } else {
                        long days = totalMinutes / 1440;
                        long hours = (totalMinutes % 1440) / 60;
                        msg += "，剩余 " + days + " 天";
                        if (hours > 0) msg += " " + hours + " 小时";
                    }
                } else {
                    // 封禁已过期，自动解封
                    user.setStatus(1);
                    user.setBanUntil(null);
                    userMapper.updateById(user);
                    String token = jwtUtils.generateToken(user.getId(), user.getUsername(), rememberMe);
                    return Map.of("token", token, "userId", user.getId(),
                                  "username", user.getUsername(), "role", user.getRole());
                }
            }
            throw new BusinessException(403, msg);
        }
        String token = jwtUtils.generateToken(user.getId(), user.getUsername(), rememberMe);
        return Map.of("token", token, "userId", user.getId(),
                      "username", user.getUsername(), "role", user.getRole());
    }

    @Override
    public UserVO getProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        return toVO(user);
    }

    @Override
    public UserVO getCurrentUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        return toVO(user);
    }

    @Override
    public void updateProfile(Long userId, String avatar, String bio, String skills) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        if (avatar != null) user.setAvatar(avatar);
        if (bio != null) user.setBio(org.jsoup.Jsoup.clean(bio, org.jsoup.safety.Safelist.none()));
        if (skills != null) user.setSkills(org.jsoup.Jsoup.clean(skills, org.jsoup.safety.Safelist.none()));
        userMapper.updateById(user);
    }

    @Override
    public User getById(Long userId) {
        return userMapper.selectById(userId);
    }

    @Override
    public List<UserVO> getRecommendedAuthors() {
        // 按作者获赞总数排序
        List<Map<String, Object>> likesMap = articleMapper.sumLikesByAuthor();
        java.util.Map<Long, Long> likesByAuthor = new HashMap<>();
        for (Map<String, Object> row : likesMap) {
            Long authorId = ((Number) row.get("authorId")).longValue();
            Long totalLikes = ((Number) row.get("totalLikes")).longValue();
            likesByAuthor.put(authorId, totalLikes);
        }

        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getIsRecommended, 1);
        List<User> users = userMapper.selectList(wrapper);
        List<UserVO> voList = new ArrayList<>();
        for (User u : users) {
            UserVO vo = toVO(u);
            vo.setTotalLikes(likesByAuthor.getOrDefault(u.getId(), 0L));
            voList.add(vo);
        }
        voList.sort((a, b) -> Long.compare(
                b.getTotalLikes() != null ? b.getTotalLikes() : 0,
                a.getTotalLikes() != null ? a.getTotalLikes() : 0));
        if (voList.size() > 5) voList = voList.subList(0, 5);
        return voList;
    }

    @Override
    public List<UserVO> getAllUsers(int page, int size) {
        Page<User> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> userPage = userMapper.selectPage(pageParam, wrapper);
        List<UserVO> voList = new ArrayList<>();
        for (User u : userPage.getRecords()) {
            voList.add(toVO(u));
        }
        return voList;
    }

    @Override
    public long countAllUsers() {
        return userMapper.selectCount(null);
    }

    @Override
    public void setRecommended(Long userId, boolean recommended) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        user.setIsRecommended(recommended ? 1 : 0);
        userMapper.updateById(user);
    }

    @Override
    public void banUser(Long userId, boolean ban, Integer hours, boolean permanent) {
        User user = userMapper.selectById(userId);
        if (user == null) throw new BusinessException("User not found");
        if (user.getRole() == 1) throw new BusinessException("不能封禁管理员");
        if (ban) {
            user.setStatus(0);
            if (permanent) {
                user.setBanUntil(null); // null = 永久封禁
            } else {
                int h = (hours != null && hours > 0) ? hours : 168; // 默认7天
                user.setBanUntil(LocalDateTime.now().plusHours(h));
            }
        } else {
            user.setStatus(1);
            user.setBanUntil(null);
        }
        userMapper.updateById(user);
    }

    private UserVO toVO(User user) {
        return UserVO.builder()
                .id(user.getId()).username(user.getUsername())
                .email(user.getEmail()).avatar(user.getAvatar())
                .bio(user.getBio()).skills(user.getSkills())
                .role(user.getRole())
                .isRecommended(user.getIsRecommended())
                .status(user.getStatus())
                .banUntil(user.getBanUntil())
                .createTime(user.getCreateTime())
                .build();
    }
}
